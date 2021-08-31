package Graphics3D.Shapes

import Graphics3D.Utils._
import Graphics3D.BaseObjects._

import scala.math.{min, max, abs}

case class Intersection(shape1: RMShape, shape2: RMShape) extends RMShape {
  override val material: Material = shape1.material
  override def getDistance(point: Vec3): Double = max(shape1.getDistance(point), shape2.getDistance(point))
}

case class Cut(shape: RMShape, cut: RMShape) extends RMShape {
  override val material: Material = shape.material
  override def getDistance(point: Vec3): Double = max(shape.getDistance(point), -cut.getDistance(point))
}

case class Blend(shape1: RMShape, shape2: RMShape, smoothness: Double) extends RMShape {
  override val material: Material = shape1.material

  def smoothMin(a: Double, b: Double): Double = {
    val h = max(smoothness - abs(a - b), 0) / smoothness
    min(a, b) - h * h * h * smoothness / 6.0
  }
  override def getDistance(point: Vec3): Double = smoothMin(shape1.getDistance(point), shape2.getDistance(point))
}