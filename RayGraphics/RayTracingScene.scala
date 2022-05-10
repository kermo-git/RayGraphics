package RayGraphics

import scala.annotation.tailrec

import RayGraphics.Components._
import RayGraphics.Geometry.Vec3
import RayGraphics.LinearColors.BLACK
import RayGraphics.Textures.Components.Texture

case class RayTracingScene[M](shapes: List[RTShape[M]],
                              background: Texture = _ => BLACK,
                              override val lights: List[PointLight] = Nil) extends Scene[M](lights) {

  type Shape = RTShape[M]
  type ShapeDist = Option[(Shape, Double)]

  override def trace(origin: Vec3, direction: Vec3): RayHit[M] = {
    val shapeDist = shapes.foldLeft[ShapeDist](None)((prevResult, nextShape) =>
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

    shapeDist match {
      case None => Nohit(background(direction))
      case Some((shape, dist)) =>
        val hitPoint = origin + direction * dist
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
    val pointToPoint = new Vec3(point2, point1)
    val dist = pointToPoint.length
    val direction = pointToPoint.normalize

    @tailrec
    def shadowTest(_shapes: List[Shape]): Boolean = _shapes match {
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
}
