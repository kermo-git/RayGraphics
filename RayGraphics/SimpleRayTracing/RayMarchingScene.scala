package RayGraphics.SimpleRayTracing

import RayGraphics.Geometry.Vec3
import RayGraphics.LinearColors.BLACK
import RayGraphics.Components._
import RayGraphics.RayObjectFunctions.{HitInfo, marchRay, rayMarchingVisibility}
import RayGraphics.Textures.Components.TextureFunction
import Components.{Light, Material, Scene}

class RayMarchingScene(camera: Camera,
                       maxBounces: Int = 5,
                       pointLights: List[Light] = Nil,
                       background: TextureFunction = _ => BLACK,
                       backgroundScale: Double = 1,
                       val shapes: List[RMShape[Material]],
                      )
  extends Scene(
    camera, maxBounces, pointLights, background, backgroundScale
  ) {

  override def visibility(point1: Vec3, point2: Vec3): Boolean =
    rayMarchingVisibility(shapes, point1, point2)

  def traceRay(origin: Vec3, direction: Vec3): Option[HitInfo[Material]] =
    marchRay(shapes, origin, direction, 100)
}
