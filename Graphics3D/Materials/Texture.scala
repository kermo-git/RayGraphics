package Graphics3D.Materials

import scala.math.pow

import Graphics3D.GeometryUtils._
import Graphics3D.Colors.Color
import Graphics3D.Components._

case class Texture(textureFunction: TextureFunction, textureScale: Double, shininess: Double = 128) extends Material {

  override def shade[O <: Shape](
    scene: Scene[O], incident: Vec3, hitPoint: Vec3, normal: Vec3, depth: Int, inside: Boolean
  ): Color = {
    val biasedHitPoint = hitPoint + normal * scene.rayHitBias
    val textureColor = textureFunction(hitPoint * textureScale)

    def addLight(color: Color, light: Light): Color = {
      val shadow = if (scene.renderShadows) scene.getShadow(biasedHitPoint, light) else 1
      if (shadow > 0) {

        val lightVec = new Vec3(hitPoint, light.location).normalize
        val diffuseIntensity = lightVec dot normal

        if (diffuseIntensity > 0) {
          val diffuseColor = textureColor * light.color * diffuseIntensity * shadow

          val specularIntensity = pow((lightVec - incident).normalize dot normal, shininess)
          if (specularIntensity > 0)
            color + diffuseColor + (light.color * specularIntensity * shadow)
          else
            color + diffuseColor
        } else color
      } else color
    }
    scene.lights.foldLeft(textureColor * 0.1)(addLight)
  }
}
