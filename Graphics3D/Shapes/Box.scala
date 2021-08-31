package Graphics3D.Shapes

import scala.math.{abs, max, sqrt}

import Graphics3D.BaseObjects._
import Graphics3D.Utils._
import Graphics3D.Materials.Matte

case class Box(lenX: Double, lenY: Double, lenZ: Double,
               override val transformation: Transformation,
               override val material: Material = Matte()) extends OriginRTShape with RMShape {

  override def getNormal(point: Vec3): Vec3 = {
    val t = point * transformation.fullInverse

    val absX = abs(t.x) + SURFACE_BIAS
    val absZ = abs(t.z) + SURFACE_BIAS

    val normal = if (absX < maxX) { if (absZ < maxZ) unitY else unitZ } else unitX

    normal * transformation.rotation
  }

  override def getRayHitAtObjectSpace(origin: Vec3, direction: Vec3): Option[RayHit] = {

    def testNextSide(prevHit: Option[RayHit], nextTest: HitTest): Option[RayHit] = {
      val distance = nextTest.getHitDist(origin, direction)

      if (distance < 0)
        prevHit
      else prevHit match {
        case None => nextTest.getHit(origin, direction, distance)
        case Some(RayHit(_, prevDist)) =>
          if (distance < prevDist)
            nextTest.getHit(origin, direction, distance) match {
              case None => prevHit
              case x => x
            }
          else
            prevHit
      }
    }
    tests.foldLeft[Option[RayHit]](None)(testNextSide)
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
    def getHit(origin: Vec3, direction: Vec3, distance: Double): Option[RayHit] = {
      val hit = origin + direction * distance
      if (inBounds(hit))
        Some(RayHit(hit, distance))
      else
        None
    }
  }

  private case class HitTestX(xPlanePos: Double) extends HitTest{
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
