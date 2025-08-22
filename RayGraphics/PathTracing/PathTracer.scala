package RayGraphics.PathTracing

import scala.math.{max, random}
import RayGraphics.*
import Geometry.*
import LinearColors.{BLACK, WHITE}
import RayGraphics.Components.*
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

  def castRay(origin: Vec3, direction: Vec3): Color = {
    var result = BLACK
    var currentThroughput = WHITE
    var currentHitPoint = origin
    var currentDirection = direction

    while (true) {
      scene.trace(currentHitPoint, currentDirection) match {
        case Nohit(color) => return result + color * currentThroughput
        case HitInfo(material, hitPoint, normal) =>
          result += currentThroughput * material.emission
          val brdfResult = material.evaluate(direction.invert, normal)
          val nextThroughput = currentThroughput * brdfResult.albedo

          val p = max(nextThroughput.red, max(nextThroughput.green, nextThroughput.blue))
          if (random() > p)
            return result

          currentThroughput = nextThroughput * (1 / p)
          currentHitPoint = hitPoint
          currentDirection = brdfResult.sample
      }
    }
    result
  }
}