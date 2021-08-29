package Graphics3D.Shapes

import Graphics3D.BaseObjects._
import Graphics3D.Utils._

case class Plane(point: Vec3 = origin, normal: Vec3 = unitY,
                 override val material: Material) extends Shape(material) with RTShape with RMShape {

  private val planeBias = point dot normal

  override def getRayHit(origin: Vec3, direction: Vec3): Option[RayHit] = {
    val normalDotDir = normal dot direction
    if (normalDotDir == 0)
      None
    else {
      val dist = (planeBias - (normal dot origin)) / normalDotDir
      if (dist < 0)
        None
      else {
        val hitPoint = origin + direction * dist
        Some(RayHit(dist, hitPoint, normal, material))
      }
    }
  }

  override def getNormal(point: Vec3): Vec3 = normal
  override def getDistance(point: Vec3): Double = (normal dot point) - planeBias
}
