package RayGraphics

import scala.annotation.tailrec

import RayGraphics.Components.{PointLight, PointLightScene, RMShape}
import RayGraphics.Geometry.Vec3
import RayGraphics.LinearColors.BLACK
import RayGraphics.Textures.Components.Texture

class RMPointLightScene[M](
                         maxDist: Double,
                         shapes: List[RMShape[M]],
                         background: Texture = _ => BLACK,
                         override val lights: List[PointLight]
                       )
  extends RayMarchingScene[M](maxDist, shapes, background) with PointLightScene[M] {

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
}
