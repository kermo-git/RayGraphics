package Graphics3D.Scenes

import Graphics3D.Geometry.Vec3
import Graphics3D.Color
import Graphics3D.LinearColors.BLACK
import Graphics3D.Components._
import Graphics3D.RayObjectFunctions.{marchRay, softShadowVisibility}

class RayMarchingScene(camera: Camera,
                       maxBounces: Int = 5,
                       pointLights: List[PointLight] = Nil,

                       val shapes: List[RMShape],
                       val background: TextureFunction = _ => BLACK,
                       val backGroundScale: Double = 1
                      )
  extends Scene(
    camera, maxBounces, pointLights
  ) {

  override def castRay(origin: Vec3, direction: Vec3, depth: Int = 0, inside: Boolean = false): Color = {
    if (depth > maxBounces) BLACK
    else marchRay(shapes, origin, direction, 100) match {
      case None => background(direction * backGroundScale)
      case Some((shape, hitPoint)) =>
        val normal = shape.getNormal(hitPoint)
        val trueNormal = if ((direction dot normal) > 0) normal.invert else normal
        shape.material.shade(this, direction, hitPoint, trueNormal, depth, inside)
    }
  }

  override def visibility(point: Vec3, light: PointLight): Double =
    softShadowVisibility(shapes, point, light, 0.5)
}
