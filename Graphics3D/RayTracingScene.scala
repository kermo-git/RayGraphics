package Graphics3D

import BaseObjects._, Colors._, Utils._

import scala.annotation.tailrec

class RayTracingScene(
  imageWidth: Int,
  imageHeight: Int,
  FOVDegrees: Int = 70,

  maxBounces: Int = 5,
  rayHitBias: Double = SURFACE_BIAS,
  renderShadows: Boolean = true,

  lights: List[Light],
  shapes: List[RTShape]
) extends Scene[RTShape](imageWidth, imageHeight, FOVDegrees, maxBounces, rayHitBias, renderShadows, lights, shapes) {

  type ShapeHit = Option[(RTShape, Vec3, Double)]

  override def castRay(origin: Vec3, direction: Vec3, depth: Int, inside: Boolean): Color = {
    def testNextShape(prevHit: ShapeHit, nextShape: RTShape): ShapeHit = {
      val newHit = nextShape.getRayHit(origin, direction)
      newHit match {
        case None => prevHit
        case Some(RayHit(nextHitPoint, nextDist)) => prevHit match {
          case None => Some(nextShape, nextHitPoint, nextDist)
          case Some((_, _, prevDist)) =>
            if (prevDist < nextDist)
              prevHit
            else
              Some(nextShape, nextHitPoint, nextDist)
        }
      }
    }
    shapes.foldLeft[ShapeHit](None)(testNextShape) match {
      case None => BLACK
      case Some((shape, hitPoint, _)) =>
        val normal = shape.getNormal(hitPoint)
        val trueNormal = if ((direction dot normal) > 0) normal.invert else normal
        shape.material.shade(this, direction, hitPoint, trueNormal, depth, inside)
    }
  }

  override def getShadow(point: Vec3, light: Light): Double = {
    val pointToLight = new Vec3(light.location, point)
    val distToLight = pointToLight.length
    val shadowRayDirection = pointToLight.normalize

    @tailrec
    def shadowTest(_shapes: List[RTShape]): Double = _shapes match {
      case Nil => 1
      case shape :: tail => shape.getRayHit(light.location, shadowRayDirection) match {
        case None => shadowTest(tail)
        case Some(RayHit(_, distance)) =>
          if (distance < distToLight) 0
          else shadowTest(tail)
      }
    }
    shadowTest(shapes)
  }
}
