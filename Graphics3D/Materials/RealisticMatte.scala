package Graphics3D.Materials

import scala.math.{pow, Pi}

import Graphics3D.Components._
import Graphics3D.Colors._
import Graphics3D.GeometryUtils._

case class RealisticMatte(color: Color = LIGHT_GRAY) extends Material {

  val normColor: Color = color * (1 / Pi)

  override def shade[O <: Shape](
    scene: Scene[O], incident: Vec3, hitPoint: Vec3, normal: Vec3, depth: Int, inside: Boolean
  ): Color = {
    val biasedHitPoint = hitPoint + normal * scene.rayHitBias

    def addLight(color: Color, light: Light): Color = {
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
