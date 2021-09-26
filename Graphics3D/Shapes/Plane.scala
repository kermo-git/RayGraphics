package Graphics3D.Shapes

import Graphics3D.Geometry._
import Graphics3D.Components._

case class Plane[M](point: Vec3 = ORIGIN, normal: Vec3 = UNIT_Y,
                 override val material: M = null) extends RTShape[M] with RMShape[M] {

  private val planeBias = point dot normal

  override def getNormal(point: Vec3): Vec3 = normal
  override def getDistance(point: Vec3): Double = (normal dot point) - planeBias

  override def getRayHitDist(origin: Vec3, direction: Vec3): Option[Double] = {
    val normalDotDir = normal dot direction
    if (normalDotDir == 0)
      None
    else {
      val distance = (planeBias - (normal dot origin)) / normalDotDir
      if (distance < 0) None else Some(distance)
    }
  }
}
