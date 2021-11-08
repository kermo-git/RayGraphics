package Graphics3D.Vanilla

import Graphics3D.Geometry.Vec3
import Graphics3D.RayObjectFunctions
import Graphics3D.LinearColors.BLACK
import Graphics3D.Components.{Camera, RTShape}
import Graphics3D.RayObjectFunctions.{HitInfo, rayTracingVisibility}
import Graphics3D.Textures.Components.TextureFunction
import Components._

class RayTracingScene(camera: Camera,
                      maxBounces: Int = 5,
                      pointLights: List[Light] = Nil,
                      background: TextureFunction = _ => BLACK,
                      backgroundScale: Double = 1,
                      val shapes: List[RTShape[Material]],
                     )
  extends Scene(
    camera, maxBounces, pointLights, background, backgroundScale
  ) {

  override def visibility(point1: Vec3, point2: Vec3): Boolean =
    rayTracingVisibility(shapes, point1, point2)

  def traceRay(origin: Vec3, direction: Vec3): Option[HitInfo[Material]] =
    RayObjectFunctions.traceRay(shapes, origin, direction)
}
