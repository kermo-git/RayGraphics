package RayGraphics

import scala.annotation.tailrec

import RayGraphics.Components.{PointLight, PointLightScene, RTShape}
import RayGraphics.Geometry.Vec3
import RayGraphics.LinearColors.BLACK
import RayGraphics.Textures.Components.Texture

class RTPointLightScene[M](
                            shapes: List[RTShape[M]],
                            background: Texture = _ => BLACK,
                            override val lights: List[PointLight]
                          )
  extends RayTracingScene[M](shapes, background) with PointLightScene[M] {

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
