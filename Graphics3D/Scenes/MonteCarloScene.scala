package Graphics3D.Scenes

import scala.annotation.tailrec
import Graphics3D.Colors.{BLACK, Color}
import Graphics3D.Components._
import Graphics3D.Geometry.{Vec3, randHemisphereVector}

class MonteCarloScene(imageWidth: Int,
                      imageHeight: Int,
                      FOVDegrees: Int = 70,

                      val samplesPerPixel: Int = 10,
                      val maxBounces: Int = 2,
                      val rayHitBias: Double = SURFACE_BIAS,

                      val background: TextureFunction = _ => BLACK,
                      val backGroundScale: Double = 1,
                      val shapes: List[RTShape[MCMaterial]]) extends Scene(imageWidth, imageHeight, FOVDegrees) {

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
    else trace[MCMaterial](shapes, origin, direction) match {
      case None => background(direction * backGroundScale)
      case Some((shape, distance)) =>
        val hitPoint = origin + direction * distance
        val _normal = shape.getNormal(hitPoint)
        val normal = if ((direction dot _normal) > 0) _normal.invert else _normal

        shape.material match {
          case MCLight(color, intensity) =>
            color * intensity // * (1.0 / (4 * Pi * distance * distance))
          case MCDiffuse(color) =>
            val nextDirection = randHemisphereVector(normal)
            val incomingLight = castRay(hitPoint + normal * rayHitBias, nextDirection, depth + 1)
            val cos = normal dot nextDirection
            color * incomingLight * 2 * cos
        }
    }
  }
}
