package Graphics3D.Materials

import Graphics3D.BaseObjects._
import Graphics3D.Config._
import Graphics3D._

class Metal(diffuse: Color, specular: Color = WHITE,
            shininess: Double = 0.5 * 128, val ior: Double = 1.3,
            val reflectivity: Double = 0) extends Matte(diffuse, specular, shininess) {

  override def shade[O <: Shape](
    scene: Scene[O], incident: Vec3, hitPoint: Vec3, normal: Vec3, depth: Int, inside: Boolean
  ): Color = {

    val _normal = if ((incident dot normal) > 0) normal.invert else normal
    val diffuseColor = super.shade(scene, incident, hitPoint, normal, depth, inside)

    if (depth < MAX_RECURSION_DEPTH) {
      val cos = -(incident dot _normal)
      val reflectionRatio = reflectivity + (1 - reflectivity) * schlick(1, ior, cos)

      val reflectedRay = reflection(incident, _normal)
      val reflectionColor = scene.castRay(hitPoint + _normal * RAY_HIT_BIAS, reflectedRay, depth + 1)

      diffuseColor * (1 - reflectionRatio) + reflectionColor * reflectionRatio
    }
    else diffuseColor
  }
}
