package Graphics3D.Materials

import scala.math.Pi

import Graphics3D.Geometry._
import Graphics3D.Color, Color._
import Graphics3D.Components._

case class Diffuse(color: Color = LIGHT_GRAY) extends Material {

  val normColor: Color = color * (1 / Pi)

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
        val lightVec = new Vec3(hitPoint, light.location)
        val normLightVec = lightVec.normalize
        val diffuseIntensity = normLightVec dot normal

        if (diffuseIntensity > 0) {
          val lightDist = lightVec.length
          val falloff = 1 / (4 * Pi * lightDist * lightDist)
          color + normColor * light.energy * falloff * diffuseIntensity * shadow
        } else color
      } else color
    }
    scene.lights.foldLeft(BLACK)(addLight)
  }
}
