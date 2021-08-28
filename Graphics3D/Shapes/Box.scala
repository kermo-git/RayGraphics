package Graphics3D.Shapes

import scala.math.{abs, max, sqrt}
import Graphics3D.BaseObjects._
import Graphics3D._

class Box(val lenX: Double, val lenY: Double, val lenZ: Double, pos: Position, material: Material)
  extends Shape(material, pos) with OriginRTShape with OriginRMShape {

  def this(sideLength: Double, pos: Position, material: Material) {
    this(sideLength, sideLength, sideLength, pos, material)
  }

  private val maxX = 0.5 * lenX
  private val maxY = 0.5 * lenY
  private val maxZ = 0.5 * lenZ

  private val minX = -maxX
  private val minY = -maxY
  private val minZ = -maxZ

  abstract class HitTest {
    val hitNormal: Vec3
    def getHitDist(origin: Vec3, direction: Vec3): Double
    def inBounds(hitPoint: Vec3): Boolean
    def getHit(origin: Vec3, direction: Vec3, distance: Double): Option[RayHit] = {
      val hit = origin + direction * distance
      if (inBounds(hit))
        Some(RayHit(distance, hit, hitNormal, material))
      else
        None
    }
  }

  case class HitTestX(xPlanePos: Double) extends HitTest{
    val hitNormal: Vec3 = unitX
    def getHitDist(origin: Vec3, direction: Vec3): Double = (xPlanePos - origin.x) / direction.x
    def inBounds(hitPoint: Vec3): Boolean =
      hitPoint.y >= minY && hitPoint.y <= maxY &&
      hitPoint.z >= minZ && hitPoint.z <= maxZ
  }

  case class HitTestY(yPlanePos: Double) extends HitTest {
    val hitNormal: Vec3 = unitY
    def getHitDist(origin: Vec3, direction: Vec3): Double = (yPlanePos - origin.y) / direction.y
    def inBounds(hitPoint: Vec3): Boolean =
      hitPoint.x >= minX && hitPoint.x <= maxX &&
      hitPoint.z >= minZ && hitPoint.z <= maxZ
  }

  case class HitTestZ(zPlanePos: Double) extends HitTest {
    val hitNormal: Vec3 = unitZ
    def getHitDist(origin: Vec3, direction: Vec3): Double = (zPlanePos - origin.z) / direction.z
    def inBounds(hitPoint: Vec3): Boolean =
      hitPoint.x >= minX && hitPoint.x <= maxX &&
      hitPoint.y >= minY && hitPoint.y <= maxY
  }

  val tests: List[HitTest] = List(
    HitTestX(minX), HitTestX(maxX),
    HitTestY(minY), HitTestY(maxY),
    HitTestZ(minZ), HitTestZ(maxZ)
  )

  override def getRayHitAtObjectSpace(origin: Vec3, direction: Vec3): Option[RayHit] = {

    def testNextSide(prevHit: Option[RayHit], nextTest: HitTest): Option[RayHit] = {
      val distance = nextTest.getHitDist(origin, direction)

      if (distance < 0)
        prevHit
      else prevHit match {
        case None => nextTest.getHit(origin, direction, distance)
        case Some(RayHit(prevDist, _, _, _)) =>
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

  override def getDistanceAtObjectSpace(point: Vec3): Double = {
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
}
