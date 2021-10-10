package Graphics3D

import scala.math.{tan, toRadians}

import Geometry._
import Color._

object Components {
  trait Renderable {
    val imageWidth: Int
    val imageHeight: Int
    def getPixelColor(x: Int, y: Int): Color
  }

  type NoiseFunction = Vec3 => Double
  type TextureFunction = Vec3 => Color

  class NoiseDisplay(val imageWidth: Int,
                     val imageHeight: Int,
                     val unitSizePx: Int,
                     val noiseZ: Double = 0,
                     val noise: NoiseFunction) extends Renderable {

    override def getPixelColor(x: Int, y: Int): Color = {
      val noiseX = 1.0 * x / unitSizePx
      val noiseY = 1.0 * y / unitSizePx
      val noiseValue = noise(Vec3(noiseX, noiseY, noiseZ))
      new Color(noiseValue, noiseValue, noiseValue)
    }
  }

  class TextureDisplay(val imageWidth: Int,
                       val imageHeight: Int,
                       val unitSizePx: Int,
                       val textureZ: Double = 0,
                       val texture: TextureFunction) extends Renderable {

    override def getPixelColor(x: Int, y: Int): Color = {
      val textureX = 1.0 * x / unitSizePx
      val textureY = 1.0 * y / unitSizePx
      texture(Vec3(textureX, textureY, textureZ))
    }
  }

  case class Camera(imageWidth: Int,
                    imageHeight: Int,
                    FOVDegrees: Int) {

    private val imagePlaneWidth = 2 * tan(toRadians(FOVDegrees / 2))
    private val imagePlaneHeight = imagePlaneWidth * imageHeight / imageWidth

    def getCameraRay(x: Int, y: Int): Vec3 = {
      val _x = (imagePlaneWidth * x / imageWidth) - 0.5 * imagePlaneWidth
      val _y = (imagePlaneHeight - imagePlaneHeight * y / imageHeight) - 0.5 * imagePlaneHeight
      Vec3(_x, _y, 1).normalize
    }
  }

  abstract class Scene(val camera: Camera,
                       val maxBounces: Int,
                       val pointLights: List[PointLight]) extends Renderable {

    val imageWidth: Int = camera.imageWidth
    val imageHeight: Int = camera.imageHeight

    override def getPixelColor(x: Int, y: Int): Color = castRay(ORIGIN, camera.getCameraRay(x, y))

    def castRay(origin: Vec3, direction: Vec3, depth: Int = 0, inside: Boolean = false): Color
    def visibility(point: Vec3, light: PointLight): Double
  }

  case class PointLight(location: Vec3, color: Color = WHITE,
                        intensity: Double = 50000,
                        shadowSharpness: Int = 20) {
    val energy: Color = color * intensity
  }

  trait Material {
    def shade(scene: Scene, incident: Vec3, hitPoint: Vec3, normal: Vec3, recDepth: Int, inside: Boolean): Color
  }

  trait Shape {
    val material: Material
    def getNormal(point: Vec3): Vec3
  }

  trait RTShape extends Shape {
    def getRayHitDist(origin: Vec3, direction: Vec3): Option[Double]
  }

  val SURFACE_BIAS = 0.005

  val (incX, incY, incZ) = (
    Vec3(0.001, 0, 0),
    Vec3(0, 0.001, 0),
    Vec3(0, 0, 0.001)
  )

  trait RMShape extends Shape {
    def getDistance(point: Vec3): Double

    override def getNormal(point: Vec3): Vec3 = {
      val gradX = getDistance(point - incX) - getDistance(point + incX)
      val gradY = getDistance(point - incY) - getDistance(point + incY)
      val gradZ = getDistance(point - incZ) - getDistance(point + incZ)

      Vec3(gradX, gradY, gradZ).normalize
    }
  }
}
