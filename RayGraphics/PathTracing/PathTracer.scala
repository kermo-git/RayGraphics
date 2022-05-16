package RayGraphics.PathTracing

import scala.math.{max, random}

import RayGraphics._
import Geometry._
import RayGraphics.Color
import LinearColors.WHITE
import RayGraphics.Components._
import PathTracing.Components.Material

case class PathTracer(camera: Camera,
                      samplesPerPixel: Int,
                      scene: Scene[Material]) extends Renderable {

  val imageWidth: Int = camera.imageWidth
  val imageHeight: Int = camera.imageHeight

  private val avgMultiplier = 1.0 / samplesPerPixel

  override def getPixelColor(x: Int, y: Int): Color = {
    val samples = for (_ <- 1 to samplesPerPixel)
      yield castRay(camera.location, camera.getRandomCameraRay(x, y))
    samples.reduce(_ + _) * avgMultiplier
  }

  def castRay(origin: Vec3, direction: Vec3, throughput: Color = WHITE): Color = {
    scene.trace(origin, direction) match {
      case Nohit(color) => color * throughput
      case HitInfo(material, hitPoint, normal) =>
        val emissionValue = throughput * material.emission

        val brdfResult = material.evaluate(direction.invert, normal)
        val nextThroughput = throughput * brdfResult.albedo

        val p = max(nextThroughput.red, max(nextThroughput.green, nextThroughput.blue))

        if (random() > p)
          emissionValue
        else
          emissionValue + castRay(hitPoint, brdfResult.sample, nextThroughput * (1 / p))
    }
  }
}