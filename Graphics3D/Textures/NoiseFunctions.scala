package Graphics3D.Textures

import scala.annotation.tailrec
import scala.math.{abs, cos}
import Graphics3D._
import Components.NoiseFunction
import GeometryUtils.Vec3
import NoiseGenerator._

object NoiseFunctions {
  def perlinNoise(point: Vec3): Double = 0.5 * signedPerlinNoise(point) + 0.5
  def absPerlinNoise(point: Vec3): Double = abs(signedPerlinNoise(point))

  def fractalNoise(noiseFunction: NoiseFunction)(octaves: Int = 4, persistence: Double = 0.4)(point: Vec3): Double = {
    @tailrec
    def octave(i: Int = 1, total: Double = 0, frequency: Double = 1, amplitude: Double = 1, max_value: Double = 0): Double = {
      if (i == octaves)
        total / max_value
      else
        octave(
          i = i + 1,
          total = total + amplitude * noiseFunction(point * frequency),
          max_value = max_value + amplitude,
          amplitude = amplitude * persistence,
          frequency = 2 * frequency
        )
    }
    octave()
  }
}
