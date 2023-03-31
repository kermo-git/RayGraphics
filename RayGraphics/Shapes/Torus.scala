package RayGraphics.Shapes

import scala.math.sqrt

import RayGraphics.Geometry._
import RayGraphics.Components._

case class Torus[M](mainRadius: Double,
                    tubeRadius: Double,
                    transformation: Transformation,
                    material: M) extends OriginRMShape[M] {

  override def getNormalAtOrigin(point: Vec3): Vec3 = {
    val tubeCenter = Vec3(point.x, 0, point.z).normalize * mainRadius
    new Vec3(tubeCenter, point).normalize
  }

  override def getDistanceAtOrigin(point: Vec3): Double = {
    val x = Vec3(point.x, 0, point.z).length - mainRadius
    sqrt(x * x + point.y * point.y) - tubeRadius
  }
}
