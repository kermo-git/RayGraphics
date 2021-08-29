package Graphics3D.Materials

import Graphics3D.BaseObjects._
import Graphics3D.Colors._
import Graphics3D.Utils._

case class Glass(color: Color = WHITE, ior: Double = 1.5, reflectivity: Double = 0) extends Material {

  override def shade[O <: Shape](
    scene: Scene[O], incident: Vec3, hitPoint: Vec3, normal: Vec3, recDepth: Int, inside: Boolean
  ): Color = {

    if (recDepth > scene.maxBounces) BLACK
    else {
      val hitPointOffset = normal * scene.rayHitBias
      val reflectedRay = reflection(incident, normal)
      val reflectionColor = scene.castRay(hitPoint + hitPointOffset, reflectedRay, recDepth + 1)

      val refractedRay = if (inside)
        refraction(incident, normal, ior, 1)
      else
        refraction(incident, normal, 1, ior)

      val result = refractedRay match {
        case None => reflectionColor
        case Some(ray) =>
          val reflectionRatio = if (inside)
            reflectivity + (1 - reflectivity) * schlick(ior, 1, -(ray dot normal))
          else
            reflectivity + (1 - reflectivity) * schlick(1, ior, -(incident dot normal))

          val refractionColor = scene.castRay(hitPoint - hitPointOffset, ray, recDepth + 1, !inside)
          reflectionColor * reflectionRatio + refractionColor * (1 - reflectionRatio)
      }
      if (inside) result else result * color
    }
  }
}
