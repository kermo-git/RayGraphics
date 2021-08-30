package Graphics3D.Shapes

import Graphics3D.BaseObjects._
import Graphics3D.Utils._

import scala.math.{abs, sqrt}

case class Cylinder(height: Double, radius: Double,
                    override val trans: Transformation,
                    override val material: Material) extends OriginRTShape with RMShape {

  private val rSqr = radius * radius
  private val minY = -0.5 * height
  private val maxY = 0.5 * height

  override def getNormal(point: Vec3): Vec3 = {
    val _point = point * trans.fullInverse

    val normal =
      if (_point.y - SURFACE_BIAS < minY || _point.y + SURFACE_BIAS > maxY)
        unitY
      else
        Vec3(_point.x, 0, _point.z).normalize

    normal * trans.rotation
  }

  override def getRayHitAtObjectSpace(origin: Vec3, direction: Vec3): Option[RayHit] = {
    val a = direction.x * direction.x + direction.z * direction.z
    val b = 2 * (origin.x * direction.x + origin.z * direction.z)
    val c = origin.x * origin.x + origin.z * origin.z - rSqr

    solveQuadraticEquation(a, b, c) match {
      case None => None
      case Some((x1, x2)) =>
        val (low, high) = if (x1 < x2) (x1, x2) else (x2, x1)

        if (high < 0)
          None
        else {
          def capHit(capY: Double): Option[RayHit] = {
            val distance = (capY - origin.y) / direction.y
            val hitPoint = origin + direction * distance
            Some(RayHit(hitPoint, distance))
          }

          def sideHit(distance: Double): Option[RayHit] = Some(RayHit(origin + direction * distance, distance))

          val nearSideHitY = origin.y + low * direction.y
          val farSideHitY = origin.y + high * direction.y

          val under = origin.y < minY
          val above = origin.y > maxY

          val inInfiniteCylinder = low < 0
          val inside = !(under || above) && inInfiniteCylinder

          if (inside && nearSideHitY > minY && farSideHitY < minY)
            capHit(minY)
          else if (inside && nearSideHitY < maxY && farSideHitY > maxY)
            capHit(maxY)
          else if (under && nearSideHitY < minY && farSideHitY > minY)
            capHit(minY)
          else if (above && nearSideHitY > maxY && farSideHitY < maxY)
            capHit(maxY)
          else if (!inside && nearSideHitY >= minY && nearSideHitY <= maxY)
            sideHit(low)
          else if (inside)
            sideHit(high)
          else None
        }
    }
  }

  override def getDistance(p: Vec3): Double = {
    val point = p * trans.fullInverse

    val capDistance = abs(point.y) - maxY
    val cylinderDistance = Vec3(point.x, 0, point.z).length - radius

    val inInfiniteCylinder = cylinderDistance < 0
    val betweenCaps = capDistance < 0

    if (inInfiniteCylinder && betweenCaps) {
      if (capDistance > cylinderDistance) capDistance else cylinderDistance
    }
    else if (betweenCaps) cylinderDistance
    else if (inInfiniteCylinder) capDistance
    else sqrt(capDistance * capDistance + cylinderDistance * cylinderDistance)
  }
}
