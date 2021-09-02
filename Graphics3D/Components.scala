package Graphics3D

import scala.math.{tan, toRadians}

import Colors._, GeometryUtils._

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
                     val noise: NoiseFunction) extends Renderable {

    override def getPixelColor(x: Int, y: Int): Color = {
      val noiseX = 1.0 * x / unitSizePx
      val noiseY = 1.0 * y / unitSizePx
      val noiseValue = noise(Vec3(noiseX, noiseY, 0))
      new Color(noiseValue, noiseValue, noiseValue)
    }
  }

  class TextureDisplay(val imageWidth: Int,
                       val imageHeight: Int,
                       val unitSizePx: Int,
                       val generator: TextureFunction) extends Renderable {

    override def getPixelColor(x: Int, y: Int): Color = {
      val noiseX = 1.0 * x / unitSizePx
      val noiseY = 1.0 * y / unitSizePx
      generator(Vec3(noiseX, noiseY, 0))
    }
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

  case class Light(x: Double, y: Double, z: Double, color: Color = WHITE, shadowSharpness: Int = 20) {
    val location: Vec3 = Vec3(x, y, z)
  }

  trait Material {
    def shade[S <: Shape](scene: Scene[S], incident: Vec3, hitPoint: Vec3, normal: Vec3, recDepth: Int, inside: Boolean): Color
  }

  case class RayHit(hitPoint: Vec3, distFromOrigin: Double)

  trait Shape {
    val material: Material
    def getNormal(point: Vec3): Vec3
  }

  trait RTShape extends Shape {
    def getRayHit(origin: Vec3, direction: Vec3): Option[RayHit]
  }

  trait OriginRTShape extends RTShape {
    val transformation: Transformation

    def getRayHitAtObjectSpace(origin: Vec3, direction: Vec3): Option[RayHit]

    override def getRayHit(origin: Vec3, direction: Vec3): Option[RayHit] =
      getRayHitAtObjectSpace(
        origin * transformation.fullInverse,
        direction * transformation.rotationInverse
      ) match {
        case Some(RayHit(hitPoint, dist)) =>
          Some(RayHit(hitPoint * transformation.full, dist))
        case None => None
      }
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
