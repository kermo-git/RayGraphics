package RayGraphics.PathTracing

import scala.math.{max, random}

import RayGraphics.Color
import RayGraphics.Geometry.Vec3
import RayGraphics.Components.{Camera, HitInfo, Nohit, Scene}
import RayGraphics.LinearColors.{BLACK, WHITE}
import RayGraphics.PathTracing.Components._

// https://cescg.org/wp-content/uploads/2018/04/Vlnas-Bidirectional-Path-Tracing-1.pdf

class LightSample(val location: Vec3, val direction: Vec3, val emission: Color)

case class BidirectionalTracer(camera: Camera,
                               scene: Scene[Material],
                               getLightSample: () => LightSample) {

  class Node(val material: Material,
             val location: Vec3,
             val normal: Vec3,
             val toPrevNode: Vec3,
             val toNextNode: Vec3,
             val throughput: Color = WHITE)

  type Path = List[Node]

  def calculateSample(cameraRayDirection: Vec3): Color = {
    val lightSample = getLightSample()
    val eyePath = generatePath(camera.location, cameraRayDirection)
    val lightPath = generatePath(lightSample.location, lightSample.direction)
    lightSample.emission * connect(eyePath, lightPath)
  }

  def generatePath(origin: Vec3, direction: Vec3, throughput: Color = WHITE): List[Node] = {
    scene.trace(origin, direction) match {
      case HitInfo(material, hitPoint, normal) =>
        val brdfResult = material.evaluate(direction.invert, normal)
        val nextThroughput = brdfResult.albedo * throughput
        val p = max(nextThroughput.red, max(nextThroughput.green, nextThroughput.blue))

        if (random() < p) {
          val next = new Node(
            material = material,
            location = hitPoint,
            normal = normal,
            toPrevNode = direction.invert,
            toNextNode = brdfResult.sample,
            throughput = nextThroughput
          )
          next :: generatePath(hitPoint, brdfResult.sample, nextThroughput * (1 / p))
        } else Nil
      case Nohit(_) => Nil
    }
  }

  def connect(eyePath: Path, lightPath: Path): Color = {
    eyePath match {
      case e1 :: e2 :: ePath =>
        lightPath match {
          case l1 :: l2 :: lPath =>
            e2.throughput * l2.throughput * connect(e1, l1) +
              connect(eyePath, l2 :: lPath) +
              connect(e2 :: ePath, lightPath) +
              connect(e2 :: ePath, l2 :: lPath)
          case l1 :: Nil =>
            e2.throughput * connect(e1, l1) +
              connect(e2 :: ePath, lightPath)
        }
      case e1 :: Nil =>
        lightPath match {
          case l1 :: l2 :: lPath =>
            l2.throughput * connect(e1, l1) +
              connect(eyePath, l2 :: lPath)
          case l1 :: Nil => connect(e1, l1)
        }
    }
  }

  def connect(n1: Node, n2: Node): Color = {
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

  def geometricTerm(n1: Node, n2: Node): Double = {
    val n1n2 = new Vec3(n1.location, n2.location)
    val distance = n1n2.length

    val n1n2Norm = n1n2.normalize
    val n2n1Norm = n1n2Norm.invert

    (n1.normal dot n2n1Norm) * (n2.normal dot n1n2Norm) / (distance * distance)
  }
}
