package Graphics3D.Materials

import Graphics3D.BaseObjects.Material
import Graphics3D.Config.{MAX_RECURSION_DEPTH, RAY_HIT_BIAS}
import Graphics3D._

class Glass(val color: Color = WHITE, val ior: Double = 1.5, val reflectivity: Double = 0) extends Material {

  override def shade[O <: BaseObjects.Shape](
    scene: BaseObjects.Scene[O], incident: Vec3, hitPoint: Vec3, normal: Vec3, recDepth: Int, inside: Boolean
  ): Color = {

    if (recDepth > MAX_RECURSION_DEPTH) BLACK
    else {
      val _normal = if ((incident dot normal) > 0) normal.invert else normal

      val reflectedRay = reflection(incident, _normal)
      val reflectionColor = scene.castRay(hitPoint + _normal * RAY_HIT_BIAS, reflectedRay, recDepth + 1)

      val refractedRay = if (inside)
        refraction(incident, _normal, ior, 1)
      else
        refraction(incident, _normal, 1, ior)

      val result = refractedRay match {
        case None => reflectionColor
        case Some(ray) =>
          val reflectionRatio = if (inside)
            schlick(ior, 1, -(ray dot _normal))
          else
            schlick(1, ior, -(incident dot _normal))

          val refractionColor = scene.castRay(hitPoint - _normal * RAY_HIT_BIAS, ray, recDepth + 1, !inside)
          reflectionColor * reflectionRatio + refractionColor * (1 - reflectionRatio)
      }
      if (inside) result else result * color
    }
  }
}
