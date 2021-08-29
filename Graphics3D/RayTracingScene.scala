package Graphics3D

import Graphics3D.BaseObjects._
import Graphics3D.Colors._
import Graphics3D.Config._
import Graphics3D.Utils._

import scala.annotation.tailrec

class RayTracingScene(
  imageWidth: Int,
  imageHeight: Int,
  FOVDegrees: Int = 70,
  lights: List[Light],
  shapes: List[RTShape]
) extends Scene[RTShape](imageWidth, imageHeight, FOVDegrees, lights, shapes) {

  override def castRay(origin: Vec3, direction: Vec3, depth: Int, inside: Boolean): Color = {
    def testNextShape(prevHit: Option[RayHit], nextShape: RTShape): Option[RayHit] = {
      val newHit = nextShape.getRayHit(origin, direction)
      newHit match {
        case None => prevHit
        case Some(RayHit(nextDist, _, _, _)) => prevHit match {
          case None => newHit
          case Some(RayHit(prevDist, _, _, _)) =>
            if (prevDist < nextDist)
              prevHit
            else
              newHit
        }
      }
    }
    shapes.foldLeft[Option[RayHit]](None)(testNextShape) match {
      case None => BLACK
      case Some(RayHit(_, hitPoint, normal, material)) =>
        material.shade(this, direction, hitPoint, normal, depth, inside)
    }
  }

  override def getShadow(point: Vec3, light: Light): Double = {
    val pointToLight = new Vec3(light.location, point)
    val distToLight = pointToLight.length
    val shadowRayDirection = pointToLight.normalize

    @tailrec
    def shadowTest(_shapes: List[RTShape]): Double = _shapes match {
      case Nil => 1
      case shape :: tail => shape.getRayHit(light.location, shadowRayDirection) match {
        case None => shadowTest(tail)
        case Some(RayHit(distance, _, _, _)) =>
          if (distance < distToLight - RAY_HIT_BIAS) 0
          else shadowTest(tail)
      }
    }
    shadowTest(shapes)
  }
}
