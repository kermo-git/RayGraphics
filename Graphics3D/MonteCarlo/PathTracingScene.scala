package Graphics3D.MonteCarlo

import scala.math.{max, random}

import Graphics3D._
import Geometry._, LinearColors.{BLACK, WHITE}, RayObjectFunctions.traceRay
import Graphics3D.Color, Graphics3D.Components._
import Textures.Components.TextureFunction
import MonteCarlo.Components.Material

class PathTracingScene(val camera: Camera,
                       val samplesPerPixel: Int,

                       val shapes: List[RTShape[Material]],

                       val background: TextureFunction = _ => BLACK,
                       val backGroundScale: Double = 1) extends Renderable {

  val imageWidth: Int = camera.imageWidth
  val imageHeight: Int = camera.imageHeight

  private val avgMultiplier = 1.0 / samplesPerPixel

  override def getPixelColor(x: Int, y: Int): Color = {
    def sampleSum(i: Int = samplesPerPixel): Color = {
      val currentSample = castRay(ORIGIN, camera.getRandomCameraRay(x, y))
      val remainingSamples = if (i == 0) BLACK else sampleSum(i - 1)
      currentSample + remainingSamples
    }
    sampleSum() * avgMultiplier
  }

  def castRay(origin: Vec3, direction: Vec3, throughput: Color = WHITE): Color = {
      traceRay(shapes, origin, direction) match {
        case None => background(direction * backGroundScale) * throughput
        case Some((shape, distance)) =>
          val emissionValue = throughput * shape.material.emission

          val hitPoint = origin + direction * distance
          val _normal = shape.getNormal(hitPoint)
          val normal = if ((direction dot _normal) > 0) _normal.invert else _normal
          val nextOrigin = hitPoint + normal * SURFACE_BIAS

          val brdfResult = shape.material.evaluate(direction.invert, normal)
          val nextThroughput = throughput * brdfResult.color

          val p = max(nextThroughput.red, max(nextThroughput.green, nextThroughput.blue))

          if (random() > p)
            emissionValue
          else {
            emissionValue + castRay(nextOrigin, brdfResult.sample, nextThroughput * (1 / p))
          }
      }
  }
}