package Graphics3D.Vanilla

import Graphics3D.Geometry._
import Graphics3D.Color
import Graphics3D.LinearColors._
import Graphics3D.Components.SURFACE_BIAS
import Components.{Material, Scene}

case class Glass(color: Color = WHITE,
                 ior: Double = 1.5) extends Material {

  override def shade(scene: Scene,
                     incident: Vec3,
                     hitPoint: Vec3,
                     normal: Vec3,
                     depth: Int,
                     inside: Boolean): Color = {

    val reflectionColor = scene.castRay(hitPoint, reflection(incident, normal), depth + 1, inside)

    val (n1, n2) = if (inside) (ior, 1.0) else (1.0, ior)

    val result = refraction(incident, normal, n1, n2) match {
      case None => reflectionColor
      case Some(ray) =>
        val biasedHitPoint = hitPoint - normal * 2 * SURFACE_BIAS
        val cos = if (inside) -(ray dot normal) else -(incident dot normal)

        val reflectionRatio = schlick(n1, n2, cos)
        val refractionColor = scene.castRay(biasedHitPoint, ray, depth + 1, !inside)
        reflectionColor * reflectionRatio + refractionColor * (1 - reflectionRatio)
    }
    if (inside) result else result * color
  }
}
