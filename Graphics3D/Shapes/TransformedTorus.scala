package Graphics3D.Shapes

import scala.math.{abs, max, sqrt}

import Graphics3D.Components.RMShape
import Graphics3D.Geometry.{Transformation, Vec3, rotateAroundY}

case class TransformedTorus[M](mainRadius: Double,
                               tubeRadius: Double,

                               twist: Double = 0,
                               elongationX: Double = 0,
                               elongationY: Double = 0,
                               elongationZ: Double = 0,

                               transformation: Transformation,
                               stepScale: Double = 0.5,
                               material: M
                        ) extends RMShape[M] {

  override def getDistance(point: Vec3): Double = stepScale * getTorusDistance(elongate(twist(point * transformation.fullInverse)))

  def getTorusDistance(point: Vec3): Double = {
    val x = Vec3(point.x, 0, point.z).length - mainRadius
    sqrt(x * x + point.y * point.y) - tubeRadius
  }

  def elongate(point: Vec3): Vec3 = Vec3(
    max(abs(point.x) - elongationX, 0),
    max(abs(point.y) - elongationY, 0),
    max(abs(point.z) - elongationZ, 0)
  )

  def twist(point: Vec3): Vec3 = point * rotateAroundY(point.y * twist)
}
