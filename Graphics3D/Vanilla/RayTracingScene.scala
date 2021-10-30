package Graphics3D.Vanilla

import Graphics3D.Geometry.Vec3
import Graphics3D.Color
import Graphics3D.LinearColors.BLACK
import Graphics3D.Components.{Camera, RTShape}
import Graphics3D.RayObjectFunctions.{rayTracingVisibility, traceRay}
import Graphics3D.Textures.Components.TextureFunction
import Components._

class RayTracingScene(camera: Camera,
                      maxBounces: Int = 5,
                      pointLights: List[Light] = Nil,

                      val shapes: List[RTShape[Material]],
                      val background: TextureFunction = _ => BLACK,
                      val backGroundScale: Double = 1
                     )
  extends Scene(
    camera, maxBounces, pointLights
  ) {

  override def castRay(origin: Vec3, direction: Vec3, depth: Int = 0, inside: Boolean = false): Color = {
    if (depth > maxBounces) BLACK
    else traceRay(shapes, origin, direction) match {
      case None => background(direction * backGroundScale)
      case Some((shape, distance)) =>
        val hitPoint = origin + direction * distance
        val normal = shape.getNormal(hitPoint)
        val trueNormal = if ((direction dot normal) > 0) normal.invert else normal
        shape.material.shade(this, direction, hitPoint, trueNormal, depth, inside)
    }
  }

  override def visibility(point1: Vec3, point2: Vec3): Boolean =
    rayTracingVisibility(shapes, point1, point2)
}
