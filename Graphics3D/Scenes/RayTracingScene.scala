package Graphics3D.Scenes

import Graphics3D.Geometry.Vec3
import Graphics3D.Color
import Graphics3D.LinearColors.BLACK
import Graphics3D.Components._
import Graphics3D.RayObjectFunctions, RayObjectFunctions.traceRay

class RayTracingScene(camera: Camera,
                      maxBounces: Int = 5,
                      pointLights: List[PointLight] = Nil,

                      val shapes: List[RTShape],
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

  override def visibility(point: Vec3, light: PointLight): Double = RayObjectFunctions.visibility(shapes, point, light)
}
