package Graphics3D.Textures

import scala.annotation.tailrec
import scala.math.abs

import Graphics3D._
import Components.NoiseFunction
import GeometryUtils.Vec3

import NoiseUtils._

object NoiseFunctions {
  val perlin2002: NoiseFunction = perlin(perlin2002grad)(_)
  val perlin1986: NoiseFunction = perlin(perlin1986grad)(_)

  def unsignedNoise(signedNoise: NoiseFunction)(point: Vec3): Double = 0.5 * signedNoise(point) + 0.5

  def turbulence(signedNoise: NoiseFunction)(point: Vec3): Double = abs(signedNoise(point))

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
