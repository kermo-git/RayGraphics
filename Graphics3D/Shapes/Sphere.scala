package Graphics3D.Shapes

import Graphics3D.Geometry._
import Graphics3D.Components._
import Graphics3D.Materials.DummyMaterial

case class Sphere(center: Vec3, radius: Double,
                  override val material: Material = DummyMaterial()) extends RMShape with RTShape {

  override def getNormal(point: Vec3): Vec3 = new Vec3(center, point).normalize
  override def getDistance(point: Vec3): Double = new Vec3(center, point).length - radius

  val equationConst: Double = (center dot center) - radius * radius

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
