package Graphics3D.Shapes

import scala.math.{abs, max, min}

import Graphics3D.GeometryUtils._
import Graphics3D.Components._
import Graphics3D.Materials.Matte

case class NoisyShape(shape: RMShape, noise: NoiseFunction,
                      noiseFrequency: Double = 1,
                      noiseAmplifier: Double = 0.5,
                      stepScale: Double = 0.5,
                      override val material: Material = Matte()) extends RMShape {

  override def getDistance(point: Vec3): Double =
    (shape.getDistance(point) + noise(point * noiseFrequency) * noiseAmplifier) * stepScale
}

case class Intersection(shape1: RMShape, shape2: RMShape,
                        override val material: Material = Matte()) extends RMShape {

  override def getDistance(point: Vec3): Double =
    max(shape1.getDistance(point), shape2.getDistance(point))
}

case class Cut(shape: RMShape, cut: RMShape,
               override val material: Material = Matte()) extends RMShape {

  override def getDistance(point: Vec3): Double =
    max(shape.getDistance(point), -cut.getDistance(point))
}

case class Blend(smoothness: Double, shape1: RMShape, shape2: RMShape,
                 override val material: Material = Matte()) extends RMShape {

  def smoothMin(a: Double, b: Double): Double = {
    val h = max(smoothness - abs(a - b), 0) / smoothness
    min(a, b) - h * h * h * smoothness / 6.0
  }
  override def getDistance(point: Vec3): Double =
    smoothMin(shape1.getDistance(point), shape2.getDistance(point))
}