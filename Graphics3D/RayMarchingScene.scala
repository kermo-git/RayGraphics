package Graphics3D

import Graphics3D.BaseObjects._
import Graphics3D.Colors._
import Graphics3D.Config._
import Graphics3D.Utils._

import scala.annotation.tailrec
import scala.math.{abs, min}

class RayMarchingScene(
  imageWidth: Int,
  imageHeight: Int,
  FOVDegrees: Int = 70,
  lights: List[Light],
  shapes: List[RMShape]
) extends Scene[RMShape](imageWidth, imageHeight, FOVDegrees, lights, shapes) {

  type DistInfo = Option[(RMShape, Double)]
  type HitPointInfo = Option[(RMShape, Vec3)]

  override def castRay(origin: Vec3, direction: Vec3, depth: Int, inside: Boolean): Color = {
    doRayMarching(origin, direction) match {
      case Some((shape, hitPoint)) =>
        shape.material.shade(this, direction, hitPoint, shape.getNormal(hitPoint), depth, inside)
      case None => BLACK
    }
  }

  override def getShadow(point: Vec3, light: Light): Double = {
    val pointToLight = new Vec3(point, light.location)
    val totalDist = pointToLight.length
    val direction = pointToLight.normalize

    @tailrec
    def doShadowRayMarching(currentPos: Vec3, traveledDist: Double = 0, shadowValue: Double = 1): Double = {
      if (traveledDist > totalDist) shadowValue
      else {
        findClosestObject(currentPos, shapes) match {
          case Some((_, objDist)) =>
            if (objDist < RAY_HIT_THRESHOLD) 0
            else {
              val nextShadowValue = min(shadowValue, light.shadowSharpness * objDist / traveledDist)
              doShadowRayMarching(currentPos + direction * objDist, traveledDist + objDist, nextShadowValue)
            }
          case None => shadowValue
        }
      }
    }
    doShadowRayMarching(point + direction * RAY_HIT_BIAS)
  }

  @tailrec
  private def doRayMarching(origin: Vec3, direction: Vec3, traveledDist: Double = 0): HitPointInfo = {
    if (traveledDist > MAX_DIST) None
    else {
      findClosestObject(origin, shapes) match {
        case Some((shape, distance)) =>
          if (distance < RAY_HIT_THRESHOLD)
            Some((shape, origin))
          else
            doRayMarching(origin + direction * distance, direction, traveledDist + distance)
        case None => None
      }
    }
  }

  @tailrec
  private def findClosestObject(viewPoint: Vec3, _shapes: List[RMShape], prevClosest: DistInfo = None): DistInfo = _shapes match {
    case shape :: tail =>
      val nextDist = abs(shape.getDistance(viewPoint))
      val nextResult = Some((shape, nextDist))

      if (nextDist < RAY_HIT_THRESHOLD)
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
