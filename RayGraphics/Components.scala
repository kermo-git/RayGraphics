package RayGraphics

import scala.math.{tan, toRadians}

import Geometry._

object Components {
  trait Renderable {
    val imageWidth: Int
    val imageHeight: Int
    def getPixelColor(x: Int, y: Int): Color
  }

  case class Camera(imageWidth: Int,
                    imageHeight: Int,
                    FOVDegrees: Int) {

    val location: Vec3 = ORIGIN

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

  trait RayHit[+M]
  case class HitInfo[M](material: M, hitPoint: Vec3, normal: Vec3) extends RayHit[M]
  case class Nohit[M](background: Color) extends RayHit[M]

  abstract class Scene[+M] {
    def trace(origin: Vec3, direction: Vec3): RayHit[M]

    def visibility(point1: Vec3, point2: Vec3): Boolean = {
      val vec = new Vec3(point1, point2)
      trace(point1, vec.normalize) match {
        case Nohit(_) => true
        case HitInfo(_, hitPoint, _) =>
          new Vec3(point1, hitPoint).length >= vec.length
      }
    }
  }

  trait Shape[+M] {
    val material: M
    def getNormal(point: Vec3): Vec3
  }

  trait RTShape[+M] extends Shape[M] {
    def getRayHitDist(origin: Vec3, direction: Vec3): Option[Double]
  }

  val SURFACE_BIAS = 0.005

  val (incX, incY, incZ) = (
    Vec3(0.001, 0, 0),
    Vec3(0, 0.001, 0),
    Vec3(0, 0, 0.001)
  )

  trait RMShape[+M] extends Shape[M] {
    def getDistance(point: Vec3): Double

    override def getNormal(point: Vec3): Vec3 = {
      val gradX = getDistance(point - incX) - getDistance(point + incX)
      val gradY = getDistance(point - incY) - getDistance(point + incY)
      val gradZ = getDistance(point - incZ) - getDistance(point + incZ)

      Vec3(gradX, gradY, gradZ).normalize
    }
  }

  trait OriginShape[+M] extends Shape[M] {
    val transformation: Transformation

    def getNormalAtOrigin(point: Vec3): Vec3

    override def getNormal(point: Vec3): Vec3 = {
      getNormalAtOrigin(
        point * transformation.fullInverse
      ) * transformation.rotation
    }
  }

  trait OriginRTShape[+M] extends OriginShape[M] with RTShape[M] {
    def getRayHitDistAtOrigin(origin: Vec3, direction: Vec3): Option[Double]

    override def getRayHitDist(origin: Vec3, direction: Vec3): Option[Double] = {
      getRayHitDistAtOrigin(
        origin * transformation.fullInverse,
        direction * transformation.rotationInverse
      )
    }
  }

  trait OriginRMShape[+M] extends OriginShape[M] with RMShape[M] {
    def getDistanceAtOrigin(point: Vec3): Double

    override def getDistance(point: Vec3): Double = {
      getDistanceAtOrigin(point * transformation.fullInverse)
    }

    override def getNormalAtOrigin(point: Vec3): Vec3 = {
      val gradX = getDistanceAtOrigin(point - incX) - getDistanceAtOrigin(point + incX)
      val gradY = getDistanceAtOrigin(point - incY) - getDistanceAtOrigin(point + incY)
      val gradZ = getDistanceAtOrigin(point - incZ) - getDistanceAtOrigin(point + incZ)

      Vec3(gradX, gradY, gradZ).normalize
    }
  }
}
