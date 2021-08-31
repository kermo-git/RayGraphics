package Graphics3D.Shapes

import Graphics3D.BaseObjects._
import Graphics3D.Utils._

import scala.math.sqrt

case class Torus(mainRadius: Double, tubeRadius: Double, trans: Transformation,
                 override val material: Material) extends RMShape {

  override def getNormal(point: Vec3): Vec3 = {
    val t = point * trans.fullInverse
    val tubeCenter = Vec3(t.x, 0, t.z).normalize * mainRadius
    new Vec3(tubeCenter, t).normalize * trans.rotation
  }

  override def getDistance(point: Vec3): Double = {
    val t = point * trans.fullInverse
    val x = Vec3(t.x, 0, t.z).length - mainRadius
    sqrt(x * x + t.y * t.y) - tubeRadius
  }
}
