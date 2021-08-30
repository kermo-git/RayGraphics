package Graphics3D

import BaseObjects._, Colors._, Utils._

import scala.annotation.tailrec
import scala.math.{abs, min}

class RayMarchingScene(
  imageWidth: Int,
  imageHeight: Int,
  FOVDegrees: Int = 70,

  maxBounces: Int = 5,
  rayHitBias: Double = SURFACE_BIAS,
  renderShadows: Boolean = true,

  val maxDist: Double = 100,
  val rayHitThreshold: Double = 0.001,
  val softShadows: Boolean = false,

  lights: List[Light],
  shapes: List[RMShape]
) extends Scene[RMShape](imageWidth, imageHeight, FOVDegrees, maxBounces, rayHitBias, renderShadows, lights, shapes) {

  type DistInfo = Option[(RMShape, Double)]
  type HitPointInfo = Option[(RMShape, Vec3)]

  override def castRay(origin: Vec3, direction: Vec3, depth: Int, inside: Boolean): Color = {

    @tailrec
    def findHitPoint(traveledDist: Double = 0): HitPointInfo = {
      if (traveledDist > maxDist) None
      else {
        val currentPos = origin + direction * traveledDist
        findClosestObject(currentPos, shapes) match {
          case Some((shape, distance)) =>
            if (distance < rayHitThreshold)
              Some((shape, currentPos))
            else
              findHitPoint(traveledDist + distance)
          case None => None
        }
      }
    }

    findHitPoint() match {
      case Some((shape, hitPoint)) =>
        val normal = shape.getNormal(hitPoint)
        val trueNormal = if ((direction dot normal) > 0) normal.invert else normal
        shape.material.shade(this, direction, hitPoint, trueNormal, depth, inside)
      case None => BLACK
    }
  }

  override def getShadow(point: Vec3, light: Light): Double = {
    val pointToLight = new Vec3(point, light.location)
    val distToLight = pointToLight.length
    val direction = pointToLight.normalize

    @tailrec
    def doShadowRayMarching(traveledDist: Double = 0, shadowValue: Double = 1): Double = {
      if (traveledDist > distToLight) shadowValue
      else {
        val currentPos = point + direction * traveledDist
        findClosestObject(currentPos, shapes) match {
          case Some((_, objDist)) =>
            if (objDist < rayHitThreshold) 0
            else doShadowRayMarching(
              traveledDist = traveledDist + objDist,

              shadowValue = if (softShadows)
                min(shadowValue, light.shadowSharpness * objDist / traveledDist)
              else
                shadowValue
            )
          case None => shadowValue
        }
      }
    }
    doShadowRayMarching()
  }

  @tailrec
  private def findClosestObject(viewPoint: Vec3, _shapes: List[RMShape], prevClosest: DistInfo = None): DistInfo = _shapes match {
    case shape :: tail =>
      val nextDist = abs(shape.getDistance(viewPoint))
      val nextResult = Some((shape, nextDist))

      if (nextDist < rayHitThreshold)
        nextResult
      else {
        val nextClosest = prevClosest match {
          case Some((_, prevDist)) =>
            if (nextDist < prevDist) nextResult else prevClosest
          case None => nextResult
        }
        findClosestObject(viewPoint, tail, nextClosest)
      }
    case Nil => prevClosest
  }
}
