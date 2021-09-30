package Graphics3D.Materials

import Graphics3D.Geometry._
import Graphics3D.Color, Color._
import Graphics3D.Components._

case class Metal(diffuse: Color = SILVER,
                 specular: Color = WHITE,
                 shininess: Double = 128,
                 ior: Double = 1.3,
                 reflectivity: Double = 0) extends Material {

  val phong: Phong = Phong(diffuse, specular, shininess)

  override def shade(scene: Scene,
                     incident: Vec3,
                     hitPoint: Vec3,
                     normal: Vec3,
                     depth: Int,
                     inside: Boolean): Color = {

    val diffuseColor = phong.shade(scene, incident, hitPoint, normal, depth, inside)

    val biasedHitPoint = hitPoint + normal * SURFACE_BIAS
    val cos = -(incident dot normal)
    val reflectionRatio = reflectivity + (1 - reflectivity) * schlick(1, ior, cos)
    val reflectionColor = scene.castRay(biasedHitPoint, reflection(incident, normal), depth + 1)

    diffuseColor * (1 - reflectionRatio) + reflectionColor * reflectionRatio
  }
}
