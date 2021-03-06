package RayGraphics.Shapes

import RayGraphics.Geometry._
import RayGraphics.Components._

case class Sphere[M](center: Vec3,
                     radius: Double,
                     material: M) extends RMShape[M] with RTShape[M] {

  override def getNormal(point: Vec3): Vec3 = new Vec3(center, point).normalize
  override def getDistance(point: Vec3): Double = new Vec3(center, point).length - radius

  private val equationConst = (center dot center) - radius * radius

  override def getRayHitDist(origin: Vec3, direction: Vec3): Option[Double] = {
    val a = direction dot direction
    val b = 2 * ((origin dot direction) - (direction dot center))
    val c = (origin dot origin) - 2 * (origin dot center) + equationConst

    solveQuadraticEquation(a, b, c) match {
      case None => None
      case Some((x1, x2)) =>
        val (low, high) = if (x1 < x2) (x1, x2) else (x2, x1)
        if (high < 0) None else Some(if (low > 0) low else high)
    }
  }
}
