package Graphics3D

import Colors._, Utils._

import scala.math.{tan, toRadians}

object BaseObjects {
  case class Light(x: Double, y: Double, z: Double, color: Color = WHITE, shadowSharpness: Int = 20) {
    val location: Vec3 = Vec3(x, y, z)
  }

  abstract class Scene[S <: Shape](
    val imageWidth: Int,
    val imageHeight: Int,
    val FOVDegrees: Int,

    val maxBounces: Int,
    val rayHitBias: Double,
    val renderShadows: Boolean,

    val lights: List[Light],
    val shapes: List[S]
  ) {
    private val screenWidth = 2 * tan(toRadians(FOVDegrees / 2))
    private val screenHeight = screenWidth * imageHeight / imageWidth
    private val halfScreenWidth = 0.5 * screenWidth
    private val halfScreenHeight = 0.5 * screenHeight

    def render(setPixelColor: (Int, Int, Color) => Unit): Unit = {
      for (x <- 0 until imageWidth) {
        for (y <- 0 until imageHeight) {
          setPixelColor(x, y, getPixelColor(x, y))
        }
      }
    }
    def getPixelColor(x: Int, y: Int): Color = castRay(origin, getCameraRay(x, y))

    def getCameraRay(x: Int, y: Int): Vec3 = {
      val _x = (screenWidth * x / imageWidth) - halfScreenWidth
      val _y = (screenHeight - screenHeight * y / imageHeight) - halfScreenHeight
      Vec3(_x, _y, 1).normalize
    }

    def castRay(origin: Vec3, direction: Vec3, depth: Int = 0, inside: Boolean = false): Color

    def getShadow(point: Vec3, light: Light): Double
  }

  trait Material {
    def shade[S <: Shape](scene: Scene[S], incident: Vec3, hitPoint: Vec3, normal: Vec3, recDepth: Int, inside: Boolean): Color
  }

  case class RayHit(distFromOrigin: Double, hitPoint: Vec3, hitNormal: Vec3, material: Material)

  class Shape(val material: Material, val pos: Position = noMove)

  trait RTShape extends Shape {
    def getRayHit(origin: Vec3, direction: Vec3): Option[RayHit]
  }

  trait OriginRTShape extends RTShape {
    def getRayHitAtObjectSpace(origin: Vec3, direction: Vec3): Option[RayHit]

    override def getRayHit(origin: Vec3, direction: Vec3): Option[RayHit] =
      getRayHitAtObjectSpace(origin * pos.fullInv, direction * pos.rotInv) match {
        case Some(RayHit(d, p, n, m)) =>
          Some(RayHit(d, p * pos.full, n * pos.rot, m))
        case None => None
      }
  }

  trait RMShape extends Shape {
    def getDistance(point: Vec3): Double

    private val GRADIENT_EPSILON = 0.001
    private val eX = Vec3(GRADIENT_EPSILON, 0, 0)
    private val eY = Vec3(0, GRADIENT_EPSILON, 0)
    private val eZ = Vec3(0, 0, GRADIENT_EPSILON)

    def getNormal(point: Vec3): Vec3 = {
      val gradX = getDistance(point - eX) - getDistance(point + eX)
      val gradY = getDistance(point - eY) - getDistance(point + eY)
      val gradZ = getDistance(point - eZ) - getDistance(point + eZ)

      Vec3(gradX, gradY, gradZ).normalize
    }
  }

  trait OriginRMShape extends RMShape {
    def getDistanceAtObjectSpace(point: Vec3): Double
    override def getDistance(point: Vec3): Double = getDistanceAtObjectSpace(point * pos.fullInv)
  }
}
