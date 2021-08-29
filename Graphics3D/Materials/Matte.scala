package Graphics3D.Materials

import Graphics3D.BaseObjects._
import Graphics3D.Colors._
import Graphics3D.Config._
import Graphics3D.Utils._

import scala.math.pow

case class Matte(diffuse: Color, specular: Color = WHITE, shininess: Double = 0.5 * 128) extends Material {

  val ambient: Color = diffuse * 0.1

  override def shade[O <: Shape](
    scene: Scene[O], incident: Vec3, hitPoint: Vec3, normal: Vec3, depth: Int, inside: Boolean
  ): Color = {
    val _normal = if ((incident dot normal) > 0) normal.invert else normal

    def addLight(color: Color, light: Light): Color = {
      val shadow = if (SHADOWS) scene.getShadow(hitPoint, light) else 1
      if (shadow > 0) {

        val lightVec = new Vec3(light.location, hitPoint).normalize
        val diffuseIntensity = -(lightVec dot _normal)

        if (diffuseIntensity > 0) {
          val diffuseColor = diffuse * light.color * diffuseIntensity * shadow

          val specularIntensity = pow(reflection(lightVec, _normal) dot incident, shininess)
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
