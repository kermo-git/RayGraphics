package Graphics3D.Shapes

import Graphics3D.Utils._
import Graphics3D.BaseObjects._
import Graphics3D.Noise.NoiseFunction

case class NoisyShape(shape: RMShape, noise: NoiseFunction, noiseFrequency: Double,
                      noiseAmplifier: Double = 0.5, stepScale: Double = 0.5) extends RMShape {

  override val material: Material = shape.material
  private val noiseMultiplier = noiseAmplifier / noiseFrequency

  override def getDistance(point: Vec3): Double =
    (shape.getDistance(point) + noise(point * noiseFrequency) * noiseMultiplier) * stepScale
}
