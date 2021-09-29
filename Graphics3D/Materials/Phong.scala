package Graphics3D.Materials

import scala.math.pow

import Graphics3D.Geometry._
import Graphics3D.Color, Color._
import Graphics3D.Components._

case class Phong(diffuse: Color = LIGHT_GRAY,
                 specular: Color = WHITE,
                 shininess: Double = 128) extends Material {

  private val ambient = diffuse * 0.1

  override def shade(scene: PointLightScene,
                     incident: Vec3,
                     hitPoint: Vec3,
                     normal: Vec3,
                     depth: Int,
                     inside: Boolean): Color = {

    val biasedHitPoint = hitPoint + normal * SURFACE_BIAS

    def addLight(color: Color, light: PointLight): Color = {
      val shadow = if (scene.renderShadows) scene.getShadow(biasedHitPoint, light) else 1
      if (shadow > 0) {

        val lightVec = new Vec3(hitPoint, light.location).normalize
        val diffuseIntensity = lightVec dot normal

        if (diffuseIntensity > 0) {
          val diffuseColor = diffuse * light.color * diffuseIntensity * shadow

          val specularIntensity = pow((lightVec - incident).normalize dot normal, shininess)
          if (specularIntensity > 0)
            color + diffuseColor + (specular * light.color * specularIntensity * shadow)
          else
            color + diffuseColor
        } else color
      } else color
    }
    scene.lights.foldLeft(ambient)(addLight)
  }
}
