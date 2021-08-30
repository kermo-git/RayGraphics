package Graphics3D

import Colors._
import Utils._

import scala.math.{tan, toRadians}

object BaseObjects {
  case class Light(x: Double, y: Double, z: Double, color: Color = WHITE, shadowSharpness: Int = 20) {
    val location: Vec3 = Vec3(x, y, z)
  }

  trait Renderable {
    val imageWidth: Int
    val imageHeight: Int
    def getPixelColor(x: Int, y: Int): Color
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
  ) extends Renderable {

    def getPixelColor(x: Int, y: Int): Color = castRay(origin, getCameraRay(x, y))

    private val imagePlaneWidth = 2 * tan(toRadians(FOVDegrees / 2))
    private val imagePlaneHeight = imagePlaneWidth * imageHeight / imageWidth

    def getCameraRay(x: Int, y: Int): Vec3 = {
      val _x = (imagePlaneWidth * x / imageWidth) - 0.5 * imagePlaneWidth
      val _y = (imagePlaneHeight - imagePlaneHeight * y / imageHeight) - 0.5 * imagePlaneHeight
      Vec3(_x, _y, 1).normalize
    }

    def castRay(origin: Vec3, direction: Vec3, depth: Int = 0, inside: Boolean = false): Color

    def getShadow(point: Vec3, light: Light): Double
  }

  trait Material {
    def shade[S <: Shape](scene: Scene[S], incident: Vec3, hitPoint: Vec3, normal: Vec3, recDepth: Int, inside: Boolean): Color
  }

  case class RayHit(hitPoint: Vec3, distFromOrigin: Double)

  trait Shape {
    val material: Material
    val trans: Transformation = nullTransformation
    def getNormal(point: Vec3): Vec3
  }

  trait RTShape extends Shape {
    def getRayHit(origin: Vec3, direction: Vec3): Option[RayHit]
  }

  trait OriginRTShape extends RTShape {
    def getRayHitAtObjectSpace(origin: Vec3, direction: Vec3): Option[RayHit]

    override def getRayHit(origin: Vec3, direction: Vec3): Option[RayHit] =
      getRayHitAtObjectSpace(origin * trans.fullInverse, direction * trans.rotationInverse) match {
        case Some(RayHit(hitPoint, dist)) =>
          Some(RayHit(hitPoint * trans.fullTransformation, dist))
        case None => None
      }
  }

  val SURFACE_BIAS = 0.005

  val incX: Vec3 = Vec3(0.001, 0, 0)
  val incY: Vec3 = Vec3(0, 0.001, 0)
  val incZ: Vec3 = Vec3(0, 0, 0.001)

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
