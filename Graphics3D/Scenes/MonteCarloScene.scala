package Graphics3D.Scenes

import scala.annotation.tailrec

import Graphics3D.Geometry._
import Graphics3D.Color, Color.BLACK, Color.WHITE
import Graphics3D.Components._

import scala.util.Random

class MonteCarloScene(imageWidth: Int,
                      imageHeight: Int,
                      FOVDegrees: Int = 70,

                      val samplesPerPixel: Int = 10,
                      val maxBounces: Int = 2,

                      val background: TextureFunction = _ => BLACK,
                      val backGroundScale: Double = 1,
                      val shapes: List[RTShape[MonteCarloMaterial]]) extends Scene(imageWidth, imageHeight, FOVDegrees) {

  private val avgMultiplier = 1.0 / samplesPerPixel

  override def getPixelColor(x: Int, y: Int): Color = {
    @tailrec
    def addSample(color: Color, i: Int): Color = {
      if (i >= samplesPerPixel)
        color
      else {
        addSample(color + super.getPixelColor(x, y), i + 1)
      }
    }
    addSample(BLACK, 0) * avgMultiplier
  }

  override def castRay(origin: Vec3, direction: Vec3, depth: Int, inside: Boolean): Color = {
    if (depth > maxBounces) BLACK
    else trace(shapes, origin, direction) match {
      case None => background(direction * backGroundScale)
      case Some((shape, distance)) =>
        val hitPoint = origin + direction * distance
        val _normal = shape.getNormal(hitPoint)
        val normal = if ((direction dot _normal) > 0) _normal.invert else _normal
        shape.material.shade(this, direction, hitPoint, normal, depth, inside)
    }
  }
}

trait MonteCarloMaterial {
  def shade(scene: MonteCarloScene, incident: Vec3, hitPoint: Vec3, normal: Vec3, recDepth: Int, inside: Boolean): Color
}

case class AreaLight(color: Color, intensity: Double) extends MonteCarloMaterial {
  private val energy = color * intensity

  override def shade(scene: MonteCarloScene,
                     incident: Vec3, hitPoint: Vec3,
                     normal: Vec3, recDepth: Int,
                     inside: Boolean): Color = energy
}

case class MCDiffuse(color: Color) extends MonteCarloMaterial {
  override def shade(scene: MonteCarloScene,
                     incident: Vec3, hitPoint: Vec3,
                     normal: Vec3, recDepth: Int,
                     inside: Boolean): Color = {

    val nextDirection = (normal + randUnitVector).normalize
    color * scene.castRay(hitPoint + normal * SURFACE_BIAS, nextDirection, recDepth + 1)
  }
}

case class Glossy(diffuse: Color,
                  specular: Color = WHITE,
                  reflectivity: Double = 0,
                  ior: Double = 1.3,
                  roughness: Double = 0) extends MonteCarloMaterial {

  override def shade(scene: MonteCarloScene,
                     incident: Vec3, hitPoint: Vec3,
                     normal: Vec3, recDepth: Int,
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

    albedo * scene.castRay(hitPoint + normal * SURFACE_BIAS, nextDirection, recDepth + 1)
  }
}
