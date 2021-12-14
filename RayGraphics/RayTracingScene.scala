package RayGraphics

import RayGraphics.Components._
import RayGraphics.Geometry.Vec3
import RayGraphics.LinearColors.BLACK
import RayGraphics.Textures.Components.Texture

case class RayTracingScene[M](
                               shapes: List[RTShape[M]],
                               background: Texture = _ => BLACK
                             )
  extends Scene[M] {

  type Shape = RTShape[M]
  type ShapeDist = Option[(Shape, Double)]

  override def trace(origin: Vec3, direction: Vec3): RayResult = {
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
}
