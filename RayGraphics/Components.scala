package RayGraphics

import scala.math.{tan, toRadians}
import Geometry._
import RayGraphics.LinearColors.WHITE

object Components {
  trait Renderable {
    val imageWidth: Int
    val imageHeight: Int
    def getPixelColor(x: Int, y: Int): Color
  }

  case class Camera(imageWidth: Int,
                    imageHeight: Int,
                    FOVDegrees: Int) {

    private val imagePlaneWidth = 2 * tan(toRadians(FOVDegrees / 2))
    private val imagePlaneHeight = imagePlaneWidth * imageHeight / imageWidth

    def getCameraRay(x: Int, y: Int): Vec3 = {
      val imagePlaneX = (imagePlaneWidth * x / imageWidth) - 0.5 * imagePlaneWidth
      val imagePlaneY = (imagePlaneHeight - imagePlaneHeight * y / imageHeight) - 0.5 * imagePlaneHeight
      Vec3(imagePlaneX, imagePlaneY, 1).normalize
    }

    def getRandomCameraRay(x: Int, y: Int): Vec3 = {
      val randomX = x + math.random()
      val randomY = y + math.random()

      val imagePlaneX = (imagePlaneWidth * randomX / imageWidth) - 0.5 * imagePlaneWidth
      val imagePlaneY = (imagePlaneHeight - imagePlaneHeight * randomY / imageHeight) - 0.5 * imagePlaneHeight

      Vec3(imagePlaneX, imagePlaneY, 1).normalize
    }
  }

  trait Scene[M] {
    trait RayResult
    case class HitInfo(material: M, hitPoint: Vec3, normal: Vec3) extends RayResult
    case class Nohit(background: Color) extends RayResult

    def trace(origin: Vec3, direction: Vec3): RayResult
  }

  case class PointLight(location: Vec3, color: Color = WHITE)

  trait PointLightScene[M] extends Scene[M] {
    val lights: List[PointLight]
    def visibility(point1: Vec3, point2: Vec3): Boolean
  }

  trait Shape[M] {
    val material: M
    def getNormal(point: Vec3): Vec3
  }

  trait RTShape[M] extends Shape[M] {
    def getRayHitDist(origin: Vec3, direction: Vec3): Option[Double]
  }

  val SURFACE_BIAS = 0.005

  val (incX, incY, incZ) = (
    Vec3(0.001, 0, 0),
    Vec3(0, 0.001, 0),
    Vec3(0, 0, 0.001)
  )

  trait RMShape[M] extends Shape[M] {
    def getDistance(point: Vec3): Double

    override def getNormal(point: Vec3): Vec3 = {
      val gradX = getDistance(point - incX) - getDistance(point + incX)
      val gradY = getDistance(point - incY) - getDistance(point + incY)
      val gradZ = getDistance(point - incZ) - getDistance(point + incZ)

      Vec3(gradX, gradY, gradZ).normalize
    }
  }
}
