package Graphics3D.Shapes

import Graphics3D.BaseObjects._
import Graphics3D.Utils._

import scala.math.sqrt

case class Torus(mainRadius: Double, tubeRadius: Double,
                 override val pos: Position,
                 override val material: Material) extends OriginRMShape {

  override def getNormal(point: Vec3): Vec3 = {
    val transformed = point * pos.fullInv
    val tubeCenter = Vec3(transformed.x, 0, transformed.z).normalize * mainRadius
    new Vec3(tubeCenter, transformed).normalize * pos.rot
  }

  override def getDistanceAtObjectSpace(point: Vec3): Double = {
    val x = Vec3(point.x, 0, point.z).length - mainRadius
    sqrt(x * x + point.y * point.y) - tubeRadius
  }
}
