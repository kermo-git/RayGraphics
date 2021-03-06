package RayGraphics.Shapes

import scala.math.{abs, max, min}

import RayGraphics.Geometry._
import RayGraphics.Components._
import RayGraphics.Textures.Components.Noise

case class NoisyShape[M](shape: RMShape[M],
                         noise: Noise,
                         noiseFrequency: Double = 1,
                         noiseAmplifier: Double = 0.5,
                         stepScale: Double = 0.5,
                         material: M) extends RMShape[M] {

  override def getDistance(point: Vec3): Double =
    (shape.getDistance(point) + noise(point * noiseFrequency) * noiseAmplifier) * stepScale
}

case class Intersection[M](shape1: RMShape[M], shape2: RMShape[M], material: M) extends RMShape[M] {

  override def getDistance(point: Vec3): Double =
    max(shape1.getDistance(point), shape2.getDistance(point))
}

case class Cut[M](shape: RMShape[M], cut: RMShape[M], material: M) extends RMShape[M] {

  override def getDistance(point: Vec3): Double =
    max(shape.getDistance(point), -cut.getDistance(point))
}

case class Blend[M](smoothness: Double, shape1: RMShape[M], shape2: RMShape[M],
                    material: M) extends RMShape[M] {

  def smoothMin(a: Double, b: Double): Double = {
    val h = max(smoothness - abs(a - b), 0) / smoothness
    min(a, b) - h * h * h * smoothness / 6.0
  }
  override def getDistance(point: Vec3): Double =
    smoothMin(shape1.getDistance(point), shape2.getDistance(point))
}