package RayGraphics.Shapes

import scala.math.{abs, sqrt, min, max}

import RayGraphics.Geometry._
import RayGraphics.Components._

case class Box[M](lenX: Double,
                  lenY: Double,
                  lenZ: Double,
                  transformation: Transformation,
                  material: M) extends OriginRTShape[M] with OriginRMShape[M] {

  private val (maxX, maxY, maxZ, minX, minY, minZ) = (
     0.5 * lenX,
     0.5 * lenY,
     0.5 * lenZ,
    -0.5 * lenX,
    -0.5 * lenY,
    -0.5 * lenZ
  )

  override def getNormalAtOrigin(point: Vec3): Vec3 = {
    val absX = abs(point.x) + SURFACE_BIAS
    val absZ = abs(point.z) + SURFACE_BIAS
    if (absX < maxX) { if (absZ < maxZ) UNIT_Y else UNIT_Z } else UNIT_X
  }

  override def getDistanceAtOrigin(point: Vec3): Double = {
    val distX = abs(point.x) - maxX
    val distY = abs(point.y) - maxY
    val distZ = abs(point.z) - maxZ

    if (distX < 0 && distY < 0 && distZ < 0)
      max(distX, max(distZ, distY))
    else
      sqrt(
        max(0, distX) * distX +
        max(0, distY) * distY +
        max(0, distZ) * distZ
      )
  }

  override def getRayHitDistAtOrigin(origin: Vec3, direction: Vec3): Option[Double] = {
    val x1 = (minX - origin.x) / direction.x
    val x2 = (maxX - origin.x) / direction.x

    val (t_minX, t_maxX): (Double, Double) = if (x1 < x2) (x1, x2) else (x2, x1)

    val y1 = (minY - origin.y) / direction.y
    val y2 = (maxY - origin.y) / direction.y

    val (t_minY, t_maxY): (Double, Double) = if (y1 < y2) (y1, y2) else (y2, y1)

    if (t_maxX < t_minY || t_maxY < t_minX)
      None
    else {
      val z1 = (minZ - origin.z) / direction.z
      val z2 = (maxZ - origin.z) / direction.z

      val (t_minZ, t_maxZ): (Double, Double) = if (z1 < z2) (z1, z2) else (z2, z1)

      if (t_maxX < t_minZ || t_maxZ < t_minX || t_maxY < t_minZ || t_maxZ < t_minY)
        None
      else {
        val outsideDist = max(t_minZ, max(t_minX, t_minY))

        if (outsideDist >= 0)
          Some(outsideDist)
        else {
          val insideDist = min(t_maxZ, min(t_maxX, t_maxY))

          if (insideDist >= 0)
            Some(insideDist)
          else
            None
        }
      }
    }
  }
}
