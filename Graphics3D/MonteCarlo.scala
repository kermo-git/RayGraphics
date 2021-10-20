package Graphics3D

import scala.annotation.tailrec
import scala.util.Random

import Graphics3D.LinearColors.{BLACK, WHITE}
import Graphics3D.Components._
import Graphics3D.Geometry._

object MonteCarlo {
  trait MonteCarloScene extends Scene {
    val samplesPerPixel: Int
    private lazy val avgMultiplier = 1.0 / samplesPerPixel

    override def getPixelColor(x: Int, y: Int): Color = {
      @tailrec
      def addSample(color: Color, i: Int): Color = {
        if (i >= samplesPerPixel)
          color
        else {
          addSample(color + castRay(ORIGIN, camera.getRandomCameraRay(x, y)), i + 1)
        }
      }

      addSample(BLACK, 0) * avgMultiplier
    }
  }

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

  case class MCGlass(color: Color = WHITE,
                     reflectivity: Double = 0,
                     ior: Double = 1.5) extends Material {

    override def shade(scene: Scene,
                       incident: Vec3, hitPoint: Vec3,
                       normal: Vec3, depth: Int,
                       inside: Boolean): Color = {

      val hitPointOffset = normal * SURFACE_BIAS
      val (n1, n2) = if (inside) (ior, 1.0) else (1.0, ior)

      val reflectionVec = reflection(incident, normal)

      val result = refraction(incident, normal, n1, n2) match {
        case None => scene.castRay(hitPoint + hitPointOffset, reflectionVec, depth + 1, inside)
        case Some(refractionVec) =>
          val cos = if (inside) -(refractionVec dot normal) else -(incident dot normal)
          val reflectionChance = lerp(reflectivity, 1, schlick(n1, n2, cos))
          val doReflection = Random.nextDouble() < reflectionChance

          if (doReflection)
            scene.castRay(hitPoint + hitPointOffset, reflectionVec, depth + 1, inside)
          else
            scene.castRay(hitPoint - hitPointOffset, refractionVec, depth + 1, !inside)
      }
      if (inside) result else result * color
    }
  }

  case class BDRFMaterial(material: BDRFInterface) extends Material {

    override def shade(scene: Scene,
                       incident: Vec3, hitPoint: Vec3,
                       normal: Vec3, depth: Int,
                       inside: Boolean): Color = {

      val view = incident.invert
      val nextDirection = material.sample(view, normal)

      val bdrf = material.BRDF(view, normal, nextDirection)
      val incoming = scene.castRay(hitPoint + normal * SURFACE_BIAS, nextDirection, depth + 1)

      material.emission + bdrf * incoming * (normal dot nextDirection)
    }
  }
}
