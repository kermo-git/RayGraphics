package Graphics3D.Vanilla

import scala.math.pow

import Graphics3D.Geometry._
import Graphics3D.Color
import Graphics3D.LinearColors._
import Graphics3D.Components.SURFACE_BIAS
import Components.{Material, Light, Scene}

case class Phong(diffuse: Color = LIGHT_GRAY,
                 specular: Color = WHITE,
                 shininess: Double = 128) extends Material {

  private val ambient = diffuse * 0.1

  override def shade(scene: Scene,
                     incident: Vec3,
                     hitPoint: Vec3,
                     normal: Vec3,
                     depth: Int,
                     inside: Boolean): Color = {

    val biasedHitPoint = hitPoint + normal * SURFACE_BIAS

    def addLight(color: Color, light: Light): Color = {
      if (scene.visibility(biasedHitPoint, light.location)) {

        val lightVec = new Vec3(hitPoint, light.location).normalize
        val diffuseIntensity = lightVec dot normal

        if (diffuseIntensity > 0) {
          val diffuseColor = diffuse * light.color * diffuseIntensity

          val specularIntensity = pow((lightVec - incident).normalize dot normal, shininess)
          if (specularIntensity > 0)
            color + diffuseColor + (specular * light.color * specularIntensity)
          else
            color + diffuseColor
        } else color
      } else color
    }
    scene.pointLights.foldLeft(ambient)(addLight)
  }
}
