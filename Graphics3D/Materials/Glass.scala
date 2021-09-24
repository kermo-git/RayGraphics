package Graphics3D.Materials

import Graphics3D.Components._
import Graphics3D.Colors._
import Graphics3D.Geometry._

case class Glass(color: Color = WHITE, ior: Double = 1.5, reflectivity: Double = 0) extends Material {

  override def shade(
    scene: PointLightScene, incident: Vec3, hitPoint: Vec3, normal: Vec3, recDepth: Int, inside: Boolean
  ): Color = {

    if (recDepth > scene.maxBounces) BLACK
    else {
      val hitPointOffset = normal * scene.rayHitBias
      val reflectionColor = scene.castRay(hitPoint + hitPointOffset, reflection(incident, normal), recDepth + 1, inside)

      val (n1, n2) = if (inside) (ior, 1.0) else (1.0, ior)

      val result = refraction(incident, normal, n1, n2) match {
        case None => reflectionColor
        case Some(ray) =>
          val cos = if (inside) -(ray dot normal) else -(incident dot normal)

          val reflectionRatio = reflectivity + (1 - reflectivity) * schlick(n1, n2, cos)
          val refractionColor = scene.castRay(hitPoint - hitPointOffset, ray, recDepth + 1, !inside)
          reflectionColor * reflectionRatio + refractionColor * (1 - reflectionRatio)
      }
      if (inside) result else result * color
    }
  }
}
