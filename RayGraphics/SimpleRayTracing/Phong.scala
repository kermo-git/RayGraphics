package RayGraphics.SimpleRayTracing

import scala.math.pow
import RayGraphics.Geometry._
import RayGraphics.Color
import RayGraphics.LinearColors._
import Components.{Material, SceneRenderer}

case class Phong(diffuse: Color = LIGHT_GRAY,
                 specular: Color = WHITE,
                 shininess: Double = 128) extends Material {

  private val ambient = diffuse * 0.1

  override def shade(renderer: SceneRenderer,
                     incident: Vec3,
                     hitPoint: Vec3,
                     normal: Vec3,
                     depth: Int,
                     inside: Boolean): Color = {

    val scene = renderer.scene

    scene.lights.foldLeft(ambient)((color, light) => {
      if (scene.visibility(hitPoint, light.location)) {

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
    })
  }
}
