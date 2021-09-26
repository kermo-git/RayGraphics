package Graphics3D.Shapes

import scala.math.{min, sqrt}

import Graphics3D.Geometry._
import Graphics3D.Components._

case class Cone[M](height: Double, radius: Double, transformation: Transformation,
                   override val material: M = null) extends RTShape[M] with RMShape[M] {

  private val normalTan = radius / height

  override def getNormal(point: Vec3): Vec3 = {
    val t = point * transformation.fullInverse

    val normal = if (t.y - SURFACE_BIAS < 0) UNIT_Y else {
      val radiusVec = Vec3(t.x, 0, t.z).normalize
      Vec3(radiusVec.x, normalTan, radiusVec.z).normalize
    }
    normal * transformation.rotation
  }

  private val k = -height / radius
  private val krec = -radius / height
  private val k_krec = k + krec

  override def getDistance(point: Vec3): Double = {
    val t = point * transformation.fullInverse

    val xp = Vec3(t.x, 0, t.z).length
    val yp = t.y
    val p = Vec3(xp, yp, 0)

    val xc = (yp + krec * xp - height) / k_krec
    val yc = k * xc + height

    if (yc > height) {
      val t = Vec3(0, height, 0)
      new Vec3(t, p).length
    }
    else if (yc < 0) {
      if (xp < radius)
        -yp
      else {
        val r = Vec3(radius, 0, 0)
        new Vec3(r, p).length
      }
    }
    else {
      if (yp < 0)
        -yp
      else {
        val c = Vec3(xc, yc, 0)
        val distance = new Vec3(c, p).length

        if (yp < k * xp + height)
          -min(yp, distance)
        else
          distance
      }
    }
  }

  override def getRayHitDist(worldOrigin: Vec3, worldDirection: Vec3): Option[Double] = {
    val (origin, direction) = (
      worldOrigin * transformation.fullInverse,
      worldDirection * transformation.rotationInverse
    )
    val coneHit: Option[Double] = getConeHitDist(origin, direction)

    val bottomDist = -origin.y / direction.y
    if (bottomDist < 0)
      coneHit
    else {
      val bottomHitPoint = origin + direction * bottomDist
      val bottomHitRadius = sqrt(
        bottomHitPoint.x * bottomHitPoint.x +
        bottomHitPoint.z * bottomHitPoint.z
      )
      if (bottomHitRadius > radius)
        coneHit
      else {
        val bottomHit = Some(bottomDist)

        coneHit match {
          case None => bottomHit
          case Some(coneDist) =>
            if (coneDist > bottomDist)
              bottomHit
            else
              coneHit
        }
      }
    }
  }

  private val hSqr_div_rSqr = (height * height) / (radius * radius)
  private val h2 = 2 * height
  private val hSqr = height * height

  private def getConeHitDist(o: Vec3, d: Vec3): Option[Double] = {
    val a = (d.x * d.x + d.z * d.z) * hSqr_div_rSqr - d.y * d.y
    val b = 2 * ((o.x * d.x + o.z * d.z) * hSqr_div_rSqr - (o.y - height) * d.y)
    val c = (o.x * o.x + o.z * o.z) * hSqr_div_rSqr - o.y * o.y + h2 * o.y - hSqr

    solveQuadraticEquation(a, b, c) match {
      case None => None
      case Some((x1, x2)) =>
        val (nearDist, farDist) = if (x1 < x2) (x1, x2) else (x2, x1)

        if (nearDist > 0 && isValidConeHit(o + d * nearDist))
          Some(nearDist)
        else if (farDist > 0 && isValidConeHit(o + d * farDist))
          Some(farDist)
        else
          None
    }
  }

  private def isValidConeHit(point: Vec3): Boolean = point.y > 0 && point.y < height
}