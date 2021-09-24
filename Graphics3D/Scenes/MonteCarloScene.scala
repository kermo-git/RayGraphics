package Graphics3D.Scenes

import scala.annotation.tailrec

import Graphics3D.Colors.{BLACK, Color}
import Graphics3D.Components._
import Graphics3D.Geometry.Vec3
import Graphics3D.{Colors, Geometry}

class MonteCarloScene(imageWidth: Int,
                      imageHeight: Int,
                      FOVDegrees: Int,

                      val samplesPerPixel: Int = 10,
                      val maxBounces: Int,
                      val rayHitBias: Double,

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

  type Shape = RTShape[MCMaterial]
  type ShapeHit = Option[(Shape, Vec3, Double)]

  override def castRay(origin: Geometry.Vec3, direction: Geometry.Vec3, depth: Int, inside: Boolean): Colors.Color = ???
}
