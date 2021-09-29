package Graphics3D.Textures

import scala.annotation.tailrec
import scala.math.{abs, min}

import Graphics3D.Geometry.Vec3
import Graphics3D.Color, Color.lerp
import Graphics3D.Components.NoiseFunction
import NoiseGenerator._

object TextureUtils {
  def perlinNoise(point: Vec3): Double = 0.5 * signedPerlinNoise(point) + 0.5
  def absPerlinNoise(point: Vec3): Double = abs(signedPerlinNoise(point))

  def octaveNoise(noiseFunction: NoiseFunction)(octaves: Int = 4, persistence: Double = 0.4)(point: Vec3): Double = {
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

  def colorBands(noise: NoiseFunction)(colors: Color*)(point: Vec3): Color =
    colors((noise(point) * colors.length).toInt)

  def smoothColorBands(noise: NoiseFunction)(colors: Color*)(point: Vec3): Color = {
    val numBands = colors.length - 1
    val noiseValue = noise(point) * numBands
    val colorIndex: Int = noiseValue.toInt
    val ratio = noiseValue - colorIndex

    lerp(colors(colorIndex), colors(colorIndex + 1), ratio)
  }

  def wood(noise: NoiseFunction, density: Int, stretchY: Double, darkColor: Color, lightColor: Color)(point: Vec3): Color = {
    val woodValue = noise(point * Vec3(1, stretchY, 1)) * density
    val bumpsValue = perlinNoise(point * Vec3(50, 10, 50))
    lerp(darkColor, lightColor, min(1, woodValue - woodValue.toInt + bumpsValue))
  }
}
