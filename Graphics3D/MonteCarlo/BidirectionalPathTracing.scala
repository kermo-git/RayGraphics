package Graphics3D.MonteCarlo

import scala.math.{Pi, max, random}

import Graphics3D.Color
import Graphics3D.Components.{Camera, RTShape, Renderable}
import Graphics3D.Geometry.Vec3
import Graphics3D.LinearColors.{BLACK, WHITE}
import Graphics3D.MonteCarlo.Components.Material
import Graphics3D.RayObjectFunctions.{HitInfo, rayTracingVisibility, traceRay}

abstract class BidirectionalPathTracing(val camera: Camera,
                                        val samplesPerPixel: Int,
                                        val shapes: List[RTShape[Material]],
                                        val lights: List[RTShape[Material]]) extends Renderable {

  val imageWidth: Int = camera.imageWidth
  val imageHeight: Int = camera.imageHeight

  override def getPixelColor(x: Int, y: Int): Color

  case class EyePathNode(material: Material,
                         location: Vec3,
                         normal: Vec3,
                         toPrevNode: Vec3,
                         energyToEye: Color,
                         previousFilter: Color)

  case class LightPathNode(material: Material,
                           location: Vec3,
                           normal: Vec3,
                           toNextNode: Vec3,
                           energyFromNextNode: Color)

  def connect(eyeSubPath: List[EyePathNode], lighSubPath: List[LightPathNode]): Color =
    eyeSubPath.foldLeft(BLACK)((color1, eyeNode) =>
      lighSubPath.foldLeft(color1)(
        (color2, lightNode) => color2 + connect(eyeNode, lightNode)
      )
    ) * (1 / (eyeSubPath.length * lighSubPath.length))

  def connect(eyeNode: EyePathNode, lightNode: LightPathNode): Color = {
    if (rayTracingVisibility(shapes, eyeNode.location, lightNode.location)) {
      val connection = new Vec3(eyeNode.location, lightNode.location)
      val direction = connection.normalize
      val distance = connection.length

      val eyeNodeFilter = eyeNode.material.BRDF(eyeNode.toPrevNode, eyeNode.normal, direction)
      val lightNodeFilter = lightNode.material.BRDF(direction.invert, lightNode.normal, lightNode.toNextNode)

      val connectionEnergy = lightNode.material.emission + lightNodeFilter * lightNode.energyFromNextNode

      val divByPDF = 1 / (Pi * distance * distance)
      eyeNode.energyToEye + eyeNode.previousFilter * eyeNodeFilter * connectionEnergy * divByPDF
    }
    else BLACK
  }

  def createEyeSubPath(prevLocation: Vec3,
                       direction: Vec3,
                       previousEnergy: Color = BLACK,
                       previousFilter: Color = WHITE): List[EyePathNode] = {

    traceRay(shapes, prevLocation, direction) match {
      case None => Nil
      case Some(HitInfo(material, hitPoint, normal)) =>
        val currentEnergy = previousEnergy + previousFilter * material.emission
        val toPrevNode = direction.invert

        val nextNode = EyePathNode(
          material,
          hitPoint,
          normal,
          toPrevNode,
          currentEnergy,
          previousFilter
        )
        val brdfResult = material.evaluate(toPrevNode, normal)
        val currentFilter = previousFilter * brdfResult.albedo

        val p = max(currentFilter.red, max(currentFilter.green, currentFilter.blue))

        if (random() > p)
          nextNode :: Nil
        else {
          nextNode :: createEyeSubPath(
            hitPoint,
            brdfResult.sample,
            currentEnergy,
            currentFilter * (1 / p)
          )
        }
    }
  }

  def createLightSubPath(origin: Vec3,
                         direction: Vec3,
                         energyFromOrigin: Color,
                         filter: Color = WHITE): List[LightPathNode] = {

    traceRay(shapes, origin, direction) match {
      case None => Nil
      case Some(HitInfo(material, hitPoint, normal)) =>

        val nextNode = LightPathNode(
          material,
          hitPoint,
          normal,
          direction.invert,
          energyFromOrigin
        )
        val brdfResult = material.evaluate(direction.invert, normal)
        val nextFilter = filter * brdfResult.albedo

        val p = max(nextFilter.red, max(nextFilter.green, nextFilter.blue))

        if (random() > p)
          nextNode :: Nil
        else {
          nextNode :: createLightSubPath(
            hitPoint,
            brdfResult.sample,
            material.emission + brdfResult.albedo * energyFromOrigin * (1 / p),
            nextFilter * (1 / p)
          )
        }
    }
  }
}
