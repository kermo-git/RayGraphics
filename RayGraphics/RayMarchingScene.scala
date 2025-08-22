package RayGraphics

import scala.annotation.tailrec
import scala.math.abs

import RayGraphics.Components._
import RayGraphics.Geometry.Vec3
import RayGraphics.LinearColors.BLACK
import RayGraphics.Textures.Components.Texture

case class RayMarchingScene[M](maxDist: Double,
                               shapes: List[RMShape[M]],
                               background: Texture = _ => BLACK) extends Scene[M] {

  type Shape = RMShape[M]
  type ShapeDist = Option[(Shape, Double)]
  type ShapeHit = Option[(Shape, Vec3)]

  val RAY_HIT_THRESHOLD = 0.001

  override def trace(origin: Vec3, direction: Vec3): RayHit[M] = {
    @tailrec
    def march(traveledDist: Double): ShapeHit = {
      if (traveledDist > maxDist) None
      else {
        val currentPos = origin + direction * traveledDist
        findClosestObject(currentPos) match {
          case Some((shape, distance)) =>
            if (distance < RAY_HIT_THRESHOLD)
              Some((shape, currentPos))
            else
              march(traveledDist + distance)
          case None => None
        }
      }
    }
    march(0) match {
      case None => Nohit(background(direction))
      case Some((shape, hitPoint)) =>
        val _normal = shape.getNormal(hitPoint)
        val normal = if ((direction dot _normal) > 0) _normal.invert else _normal

        HitInfo(
          shape.material,
          hitPoint + normal * SURFACE_BIAS,
          normal
        )
    }
  }

  override def visibility(point1: Vec3, point2: Vec3): Boolean = {
    val pointToPoint = new Vec3(point1, point2)
    val dist = pointToPoint.length
    val direction = pointToPoint.normalize

    @tailrec
    def doShadowRayMarching(traveledDist: Double): Boolean = {
      if (traveledDist > dist) true
      else {
        findClosestObject(point1 + direction * traveledDist) match {
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

  protected def findClosestObject(viewPoint: Vec3): ShapeDist = {
    @tailrec
    def helper(_shapes: List[Shape], prevClosest: ShapeDist): ShapeDist = {
      _shapes match {
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
            helper(tail, nextClosest)
          }
        case Nil => prevClosest
      }
    }
    helper(shapes, None)
  }
}
