package Graphics3D.Shapes

import scala.math.sqrt

import Graphics3D.Components._
import Graphics3D.Geometry._

case class Torus[M](mainRadius: Double, tubeRadius: Double, transformation: Transformation,
                 override val material: M = null) extends RMShape[M] {

  override def getNormal(point: Vec3): Vec3 = {
    val t = point * transformation.fullInverse
    val tubeCenter = Vec3(t.x, 0, t.z).normalize * mainRadius
    new Vec3(tubeCenter, t).normalize * transformation.rotation
  }

  override def getDistance(point: Vec3): Double = {
    val t = point * transformation.fullInverse
    val x = Vec3(t.x, 0, t.z).length - mainRadius
    sqrt(x * x + t.y * t.y) - tubeRadius
  }
}
