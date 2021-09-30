package Graphics3D.Materials

import scala.util.Random

import Graphics3D.Geometry._
import Graphics3D.Color, Color.WHITE
import Graphics3D.Components._

case class AreaLight(color: Color, intensity: Double) extends Material {
  private val energy = color * intensity

  override def shade(scene: Scene,
                     incident: Vec3, hitPoint: Vec3,
                     normal: Vec3, depth: Int,
                     inside: Boolean): Color = energy
}

case class MCDiffuse(color: Color) extends Material {
  override def shade(scene: Scene,
                     incident: Vec3, hitPoint: Vec3,
                     normal: Vec3, depth: Int,
                     inside: Boolean): Color = {

    val nextDirection = (normal + randUnitVector).normalize
    color * scene.castRay(hitPoint + normal * SURFACE_BIAS, nextDirection, depth + 1)
  }
}

case class MCTexture(texture: TextureFunction, scale: Double) extends Material {
  override def shade(scene: Scene,
                     incident: Vec3, hitPoint: Vec3,
                     normal: Vec3, depth: Int,
                     inside: Boolean): Color = {

    val nextDirection = (normal + randUnitVector).normalize
    texture(hitPoint * scale) * scene.castRay(hitPoint + normal * SURFACE_BIAS, nextDirection, depth + 1)
  }
}

case class Glossy(diffuse: Color,
                  specular: Color = WHITE,
                  reflectivity: Double = 0,
                  ior: Double = 1.3,
                  roughness: Double = 0) extends Material {

  override def shade(scene: Scene,
                     incident: Vec3, hitPoint: Vec3,
                     normal: Vec3, depth: Int,
                     inside: Boolean): Color = {

    val reflectionChance = lerp(reflectivity, 1, schlick(1, ior, -(incident dot normal)))
    val doReflection = Random.nextDouble() < reflectionChance

    val diffuseDirection = (normal + randUnitVector).normalize
    val nextDirection: Vec3 =
      if (doReflection)
        lerp(reflection(incident, normal), diffuseDirection, roughness).normalize
      else
        diffuseDirection

    val albedo = if (doReflection) specular else diffuse

    albedo * scene.castRay(hitPoint + normal * SURFACE_BIAS, nextDirection, depth + 1)
  }
}
