package Graphics3D.Shapes

import scala.math.{abs, max, sqrt}

import Graphics3D.Components._
import Graphics3D.Geometry._

case class Box[M](lenX: Double, lenY: Double, lenZ: Double, transformation: Transformation,
                  override val material: M = null) extends RTShape[M] with RMShape[M] {

  override def getNormal(point: Vec3): Vec3 = {
    val t = point * transformation.fullInverse

    val absX = abs(t.x) + SURFACE_BIAS
    val absZ = abs(t.z) + SURFACE_BIAS

    val normal = if (absX < maxX) { if (absZ < maxZ) UNIT_Y else UNIT_Z } else UNIT_X

    normal * transformation.rotation
  }

  override def getDistance(point: Vec3): Double = {
    val t = point * transformation.fullInverse

    val distX = abs(t.x) - maxX
    val distY = abs(t.y) - maxY
    val distZ = abs(t.z) - maxZ

    if (distX < 0 && distY < 0 && distZ < 0)
      max(distX, max(distZ, distY))
    else
      sqrt(
        max(0, distX) * distX +
          max(0, distY) * distY +
          max(0, distZ) * distZ
      )
  }

  override def getRayHitDist(worldOrigin: Vec3, worldDirection: Vec3): Option[Double] = {
    val (origin, direction) = (
      worldOrigin * transformation.fullInverse,
      worldDirection * transformation.rotationInverse
    )
    def testNextSide(prevHit: Option[Double], nextTest: HitTest): Option[Double] = {
      val distance = nextTest.getHitDist(origin, direction)

      if (distance < 0)
        prevHit
      else prevHit match {
        case None => nextTest.getHit(origin, direction, distance)
        case Some(prevDist) =>
          if (distance < prevDist)
            nextTest.getHit(origin, direction, distance) match {
              case None => prevHit
              case x => x
            }
          else
            prevHit
      }
    }
    tests.foldLeft[Option[Double]](None)(testNextSide)
  }

  private val (maxX, maxY, maxZ, minX, minY, minZ) = (
     0.5 * lenX,
     0.5 * lenY,
     0.5 * lenZ,
    -0.5 * lenX,
    -0.5 * lenY,
    -0.5 * lenZ
  )

  private val tests: List[HitTest] = List(
    HitTestX(minX), HitTestX(maxX),
    HitTestY(minY), HitTestY(maxY),
    HitTestZ(minZ), HitTestZ(maxZ)
  )

  private abstract class HitTest {
    def getHitDist(origin: Vec3, direction: Vec3): Double
    def inBounds(hitPoint: Vec3): Boolean
    def getHit(origin: Vec3, direction: Vec3, distance: Double): Option[Double] = {
      if (inBounds(origin + direction * distance))
        Some(distance)
      else
        None
    }
  }

  private case class HitTestX(xPlanePos: Double) extends HitTest {
    def getHitDist(origin: Vec3, direction: Vec3): Double = (xPlanePos - origin.x) / direction.x
    def inBounds(hitPoint: Vec3): Boolean =
      hitPoint.y >= minY && hitPoint.y <= maxY &&
      hitPoint.z >= minZ && hitPoint.z <= maxZ
  }

  private case class HitTestY(yPlanePos: Double) extends HitTest {
    def getHitDist(origin: Vec3, direction: Vec3): Double = (yPlanePos - origin.y) / direction.y
    def inBounds(hitPoint: Vec3): Boolean =
      hitPoint.x >= minX && hitPoint.x <= maxX &&
      hitPoint.z >= minZ && hitPoint.z <= maxZ
  }

  private case class HitTestZ(zPlanePos: Double) extends HitTest {
    def getHitDist(origin: Vec3, direction: Vec3): Double = (zPlanePos - origin.z) / direction.z
    def inBounds(hitPoint: Vec3): Boolean =
      hitPoint.x >= minX && hitPoint.x <= maxX &&
      hitPoint.y >= minY && hitPoint.y <= maxY
  }
}
