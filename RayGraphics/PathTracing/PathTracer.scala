package RayGraphics.PathTracing

import scala.math.{max, random}

import RayGraphics._
import Geometry._
import LinearColors.{BLACK, WHITE}
import RayGraphics.Color, RayGraphics.Components._
import PathTracing.Components.Material

class PathTracer(val camera: Camera,
                 val samplesPerPixel: Int,
                 val scene: Scene[Material]) extends Renderable {

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
      scene.trace(origin, direction) match {
        case scene.Nohit(color) => color * throughput
        case scene.HitInfo(material, hitPoint, normal) =>
          val emissionValue = throughput * material.emission

          val brdfResult = material.evaluate(direction.invert, normal)
          val nextThroughput = throughput * brdfResult.albedo

          val p = max(nextThroughput.red, max(nextThroughput.green, nextThroughput.blue))

          if (random() > p)
            emissionValue
          else {
            emissionValue + castRay(hitPoint, brdfResult.sample, nextThroughput * (1 / p))
          }
      }
  }
}