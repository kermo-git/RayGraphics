package Graphics3D

import scala.annotation.tailrec
import scala.math.{abs, min}

import Components._, Colors._, Geometry._

class RayMarchingScene(imageWidth: Int,
                       imageHeight: Int,
                       FOVDegrees: Int = 70,

                       maxBounces: Int = 5,
                       rayHitBias: Double = SURFACE_BIAS,

                       renderShadows: Boolean = true,
                       shadowStepMultiPlier: Double = 1,

                       val maxDist: Double = 100,
                       val rayHitThreshold: Double = 0.001,

                       background: TextureFunction = _ => BLACK,
                       backGroundScale: Double = 1,

                       lights: List[Light],
                       shapes: List[RMShape]
                      )
  extends Scene[RMShape](
    imageWidth, imageHeight, FOVDegrees, maxBounces, rayHitBias, renderShadows, background, backGroundScale, lights, shapes
  ) {

  type ShapeDist = Option[(Shape, Double)]
  type ShapeHit = Option[(Shape, Vec3)]

  override def castRay(origin: Vec3, direction: Vec3, depth: Int, inside: Boolean): Color = {

    @tailrec
    def findHitPoint(traveledDist: Double = 0): ShapeHit = {
      if (traveledDist > maxDist) None
      else {
        val currentPos = origin + direction * traveledDist
        findClosestObject(currentPos) match {
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
      case None => background(direction * backGroundScale)
      case Some((shape, hitPoint)) =>
        val normal = shape.getNormal(hitPoint)
        val trueNormal = if ((direction dot normal) > 0) normal.invert else normal
        shape.material.shade(this, direction, hitPoint, trueNormal, depth, inside)
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
        findClosestObject(point + direction * traveledDist) match {
          case Some((_, sceneDist)) =>
            if (sceneDist < rayHitThreshold) 0
            else
              doShadowRayMarching(
                traveledDist = traveledDist + shadowStepMultiPlier * sceneDist,
                shadowValue = min(shadowValue, light.shadowSharpness * sceneDist / traveledDist)
              )
          case None => shadowValue
        }
      }
    }
    doShadowRayMarching()
  }

  @tailrec
  private def findClosestObject(viewPoint: Vec3, _shapes: List[RMShape] = shapes, prevClosest: ShapeDist = None): ShapeDist = _shapes match {
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
