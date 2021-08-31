package Graphics3D.Shapes

import scala.math.{min, sqrt}

import Graphics3D.BaseObjects._
import Graphics3D.Utils._
import Graphics3D.Materials.Matte

case class Cone(height: Double, radius: Double,
                override val transformation: Transformation,
                override val material: Material = Matte()) extends OriginRTShape with RMShape {

  override def getNormal(point: Vec3): Vec3 = {
    val t = point * transformation.fullInverse

    val normal = if (t.y - SURFACE_BIAS < 0) unitY else {
      val radiusVec = Vec3(t.x, 0, t.z).normalize
      Vec3(radiusVec.x, normalTan, radiusVec.z).normalize
    }
    normal * transformation.rotation
  }

  private val hSqr_div_rSqr = (height * height) / (radius * radius)
  private val _2h = 2 * height
  private val hSqr = height * height
  private val normalTan = radius / height

  def getConeHit(origin: Vec3, direction: Vec3, distance: Double): Option[RayHit] = {
    if (distance < 0)
      None
    else {
      val hitPoint = origin + direction * distance
      if (hitPoint.y < 0 || hitPoint.y > height)
        None
      else {
        Some(RayHit(hitPoint, distance))
      }
    }
  }

  override def getRayHitAtObjectSpace(o: Vec3, d: Vec3): Option[RayHit] = {
    val a = (d.x * d.x + d.z * d.z) * hSqr_div_rSqr - d.y * d.y
    val b = 2 * ((o.x * d.x + o.z * d.z) * hSqr_div_rSqr - (o.y - height) * d.y)
    val c = (o.x * o.x + o.z * o.z) * hSqr_div_rSqr - o.y * o.y + _2h * o.y - hSqr

    val coneHit: Option[RayHit] = solveQuadraticEquation(a, b, c) match {
      case None => None
      case Some((x1, x2)) =>
        val (nearDist, farDist) = if (x1 < x2) (x1, x2) else (x2, x1)
        if (farDist > 0) {
          getConeHit(o, d, nearDist) match {
            case None => getConeHit(o, d, farDist)
            case nearHit => nearHit
          }
        }
        else None
    }
    val bottomDist = -o.y / d.y
    if (bottomDist < 0)
      coneHit
    else {
      val bottomHitPoint = o + d * bottomDist
      val bottomHitRadius = sqrt(
        bottomHitPoint.x * bottomHitPoint.x +
        bottomHitPoint.z * bottomHitPoint.z
      )
      if (bottomHitRadius > radius)
        coneHit
      else {
        val bottomHit = Some(RayHit(bottomHitPoint, bottomDist))

        coneHit match {
          case None => bottomHit
          case Some(RayHit(_, coneDist)) =>
            if (coneDist > bottomDist)
              bottomHit
            else
              coneHit
        }
      }
    }
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
        val coneHeight = k * xp + height

        if (yp < coneHeight)
          -min(yp, distance)
        else
          distance
      }
    }
  }
}