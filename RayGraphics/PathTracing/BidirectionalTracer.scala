package RayGraphics.PathTracing

import scala.math.{max, random}

import RayGraphics.Color
import RayGraphics.Geometry.Vec3
import RayGraphics.Components.{Camera, HitInfo, Nohit, Renderable, Scene}
import RayGraphics.LinearColors.{BLACK, WHITE}
import RayGraphics.PathTracing.Components._

// https://cescg.org/wp-content/uploads/2018/04/Vlnas-Bidirectional-Path-Tracing-1.pdf

case class LightSample(location: Vec3, direction: Vec3, emission: Color)

case class BidirectionalTracer(camera: Camera,
                               samplesPerPixel: Int,
                               getLightSample: () => LightSample,
                               scene: Scene[Material]) extends Renderable {

  val imageWidth: Int = camera.imageWidth
  val imageHeight: Int = camera.imageHeight

  case class Node(material: Material,
                  location: Vec3,
                  normal: Vec3,
                  toPrevNode: Vec3,
                  toNextNode: Vec3,
                  throughput: Color = WHITE)

  type Path = List[Node]
  private val avgMultiplier = 1.0 / samplesPerPixel

  override def getPixelColor(x: Int, y: Int): Color = {
    val cameraRayDirection = camera.getCameraRay(x, y)

    val samples = for (_ <- 1 to samplesPerPixel) yield {
      val lightSample = getLightSample()
      val eyePath = generatePath(camera.location, cameraRayDirection)

      if (eyePath.isEmpty)
        BLACK
      else {
        val lightPath = generatePath(lightSample.location, lightSample.direction)
        if (lightPath.isEmpty)
          BLACK
        else
          lightSample.emission * connectPaths(eyePath, lightPath) + eyePath.last.material.emission
      }
    }
    samples.reduce(_ + _) * avgMultiplier
  }

  def generatePath(origin: Vec3, direction: Vec3, throughput: Color = WHITE, result: Path = Nil): Path = {
    if (result.length >= 10)
      result
    else
      scene.trace(origin, direction) match {
        case Nohit(_) => result
        case HitInfo(material, hitPoint, normal) =>
          val brdfResult = material.evaluate(direction.invert, normal)
          val nextThroughput = brdfResult.albedo * throughput
          val p = max(nextThroughput.red, max(nextThroughput.green, nextThroughput.blue))

          val next = Node(
            material = material,
            location = hitPoint,
            normal = normal,
            toPrevNode = direction.invert,
            toNextNode = brdfResult.sample,
            throughput = nextThroughput
          )
          if (random() > p)
            next :: result
          else
            generatePath(hitPoint, brdfResult.sample, nextThroughput * (1 / p), next :: result)
      }
  }

  def connectPaths(eyePath: Path, lightPath: Path): Color = {
    def loopEyePath(eyePath: Path, lightPath: Path): Color = {
      eyePath match {
        case Nil => BLACK
        case _ :: tail =>
          loopLightPath(eyePath, lightPath) + loopEyePath(tail, lightPath)
      }
    }

    def loopLightPath(eyePath: Path, lightPath: Path): Color = {
      lightPath match {
        case Nil => BLACK
        case _ :: tail =>
          evaluate(eyePath, lightPath) + loopLightPath(eyePath, tail)
      }
    }

    def evaluate(eyePath: Path, lightPath: Path): Color = {
      (eyePath, lightPath) match {
        case (e1 :: eyeTail, l1 :: lightTail) =>
          val eThroughput = eyeTail match {
            case _ => WHITE
            case e2 :: _ => e2.throughput
          }
          val lThroughput = lightTail match {
            case _ => WHITE
            case l2 :: _ => l2.throughput
          }
          eThroughput * lThroughput * connectNodes(e1, l1)
        case _ => BLACK
      }
    }
    val weight = 1 / (eyePath.length * lightPath.length)
    loopEyePath(eyePath, lightPath) * weight
  }

  def connectNodes(n1: Node, n2: Node): Color = {
    if (!scene.visibility(n1.location, n2.location))
      BLACK
    else {
      val n1n2 = new Vec3(n1.location, n2.location)
      val distance = n1n2.length

      val n1n2Norm = n1n2.normalize
      val n2n1Norm = n1n2Norm.invert

      val brdf1 = n1.material.BRDF(n1.toPrevNode, n1.normal, n1n2Norm)
      val brdf2 = n2.material.BRDF(n2.toPrevNode, n2.normal, n2n1Norm)
      val G = (n1.normal dot n2n1Norm) * (n2.normal dot n1n2Norm) / (distance * distance)

      brdf1 * brdf2 * G
    }
  }
}
