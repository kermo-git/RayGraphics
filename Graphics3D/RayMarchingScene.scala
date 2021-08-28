package Graphics3D

import scala.math.abs
import scala.annotation.tailrec
import Graphics3D.BaseObjects._
import Graphics3D.Config.{MAX_DIST, RAY_HIT_BIAS, RAY_HIT_THRESHOLD}

class RayMarchingScene(lights: List[Light], shapes: List[RMShape]) extends Scene[RMShape](lights, shapes) {
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
    val distToLight = pointToLight.length
    val direction = pointToLight.normalize
    doShadowRayMarching(point + direction * RAY_HIT_BIAS, direction, distToLight)
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
  private def doShadowRayMarching(origin: Vec3, direction: Vec3, distToLight: Double, traveledDist: Double = 0): Double = {
    if (traveledDist > distToLight) 1
    else {
      findClosestObject(origin, shapes) match {
        case Some((_, distance)) =>
          if (distance < RAY_HIT_THRESHOLD)
            0
          else
            doShadowRayMarching(origin + direction * distance, direction, traveledDist + distance, distToLight)
        case None => 1
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
