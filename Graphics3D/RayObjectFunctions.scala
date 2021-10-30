package Graphics3D

import scala.annotation.tailrec
import scala.math.abs

import Graphics3D.Geometry.Vec3
import Graphics3D.Components._

object RayObjectFunctions {
  type ShapeDist[M] = Option[(Shape[M], Double)]
  type ShapeHit[M] = Option[(Shape[M], Vec3)]

  def traceRay[M](shapes: List[RTShape[M]], origin: Vec3, direction: Vec3): ShapeDist[M] =
    shapes.foldLeft[ShapeDist[M]](None)((prevResult, nextShape) =>
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

  def rayTracingVisibility[M](shapes: List[RTShape[M]], point1: Vec3, point2: Vec3): Boolean = {
    val pointToPoint = new Vec3(point2, point1)
    val dist = pointToPoint.length
    val direction = pointToPoint.normalize

    @tailrec
    def shadowTest(_shapes: List[RTShape[M]]): Boolean = _shapes match {
      case Nil => true
      case shape :: tail => shape.getRayHitDist(point2, direction) match {
        case None =>
          shadowTest(tail)
        case Some(distance) =>
          if (distance < dist)
            false
          else
            shadowTest(tail)
      }
    }
    shadowTest(shapes)
  }

  val RAY_HIT_THRESHOLD = 0.001

  def marchRay[M](shapes: List[RMShape[M]], origin: Vec3, direction: Vec3, maxDist: Double): ShapeHit[M] = {
    @tailrec
    def march(traveledDist: Double): ShapeHit[M] = {
      if (traveledDist > maxDist) None
      else {
        val currentPos = origin + direction * traveledDist
        findClosestObject[M](currentPos, shapes, None) match {
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

  def rayMarchingVisibility[M](shapes: List[RMShape[M]], point1: Vec3, point2: Vec3): Boolean = {
    val pointToPoint = new Vec3(point1, point2)
    val dist = pointToPoint.length
    val direction = pointToPoint.normalize

    @tailrec
    def doShadowRayMarching(traveledDist: Double): Boolean = {
      if (traveledDist > dist) true
      else {
        findClosestObject(point1 + direction * traveledDist, shapes, None) match {
          case Some((_, sceneDist)) =>
            if (sceneDist < RAY_HIT_THRESHOLD) false
            else
              doShadowRayMarching(traveledDist + sceneDist)
          case None => true
        }
      }
    }
    doShadowRayMarching(0)
  }

  @tailrec
  private def findClosestObject[M](viewPoint: Vec3, shapes: List[RMShape[M]], prevClosest: ShapeDist[M] = None): ShapeDist[M] = {
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
