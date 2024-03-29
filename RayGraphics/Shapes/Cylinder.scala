package RayGraphics.Shapes

import scala.math.{abs, sqrt}

import RayGraphics.Geometry._
import RayGraphics.Components._

case class Cylinder[M](height: Double,
                       radius: Double,
                       transformation: Transformation,
                       material: M = null) extends OriginRTShape[M] with OriginRMShape[M] {

  private val (rSqr, minY, maxY) = (radius * radius, -0.5 * height, 0.5 * height)

  override def getNormalAtOrigin(point: Vec3): Vec3 = {
    val normal =
      if (point.y - SURFACE_BIAS < minY || point.y + SURFACE_BIAS > maxY)
        UNIT_Y
      else
        Vec3(point.x, 0, point.z).normalize

    normal
  }

  override def getRayHitDistAtOrigin(origin: Vec3, direction: Vec3): Option[Double] = {
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
          def capHit(capY: Double): Option[Double] = Some((capY - origin.y) / direction.y)

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
            Some(low)
          else if (inside)
            Some(high)
          else None
        }
    }
  }

  override def getDistanceAtOrigin(point: Vec3): Double = {
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
