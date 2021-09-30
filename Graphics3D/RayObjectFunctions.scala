package Graphics3D

import scala.annotation.tailrec
import scala.math.{abs, min}

import Graphics3D.Geometry.Vec3
import Graphics3D.Components._

object RayObjectFunctions {
  type ShapeDist = Option[(Shape, Double)]
  type ShapeHit = Option[(Shape, Vec3)]

  def traceRay(shapes: List[RTShape], origin: Vec3, direction: Vec3): ShapeDist =
    shapes.foldLeft[ShapeDist](None)((prevResult, nextShape) =>
      nextShape.getRayHitDist(origin, direction) match {
        case None => prevResult
        case Some(nextDist) =>
          val nextResult = Some((nextShape, nextDist))
          prevResult match {
            case None => nextResult
            case Some((_, prevDist)) =>
              if (prevDist < nextDist)
                prevResult
              else
                nextResult
          }
      })

  def visibility(shapes: List[RTShape], point: Vec3, light: PointLight): Double = {
    val pointToLight = new Vec3(light.location, point)
    val distToLight = pointToLight.length
    val shadowRayDirection = pointToLight.normalize

    @tailrec
    def shadowTest(_shapes: List[RTShape]): Double = _shapes match {
      case Nil => 1
      case shape :: tail => shape.getRayHitDist(light.location, shadowRayDirection) match {
        case None => shadowTest(tail)
        case Some(distance) => if (distance < distToLight) 0 else shadowTest(tail)
      }
    }

    shadowTest(shapes)
  }

  val RAY_HIT_THRESHOLD = 0.001

  def marchRay(shapes: List[RMShape], origin: Vec3, direction: Vec3, maxDist: Double): ShapeHit = {
    @tailrec
    def march(traveledDist: Double): ShapeHit = {
      if (traveledDist > maxDist) None
      else {
        val currentPos = origin + direction * traveledDist
        findClosestObject(currentPos, shapes, None) match {
          case Some((shape, distance)) =>
            if (distance < RAY_HIT_THRESHOLD)
              Some((shape, currentPos))
            else
              march(traveledDist + distance)
          case None => None
        }
      }
    }
    march(0)
  }

  def softShadowVisibility(shapes: List[RMShape], point: Vec3, light: PointLight, stepScale: Double = 1): Double = {
    val pointToLight = new Vec3(point, light.location)
    val distToLight = pointToLight.length
    val direction = pointToLight.normalize

    @tailrec
    def doShadowRayMarching(traveledDist: Double = 0, shadowValue: Double = 1): Double = {
      if (traveledDist > distToLight) shadowValue
      else {
        findClosestObject(point + direction * traveledDist, shapes, None) match {
          case Some((_, sceneDist)) =>
            if (sceneDist < RAY_HIT_THRESHOLD) 0
            else
              doShadowRayMarching(
                traveledDist = traveledDist + stepScale * sceneDist,
                shadowValue = min(shadowValue, light.shadowSharpness * sceneDist / traveledDist)
              )
          case None => shadowValue
        }
      }
    }
    doShadowRayMarching()
  }

  @tailrec
  private def findClosestObject(viewPoint: Vec3, shapes: List[RMShape], prevClosest: ShapeDist = None): ShapeDist = {
    shapes match {
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
}
