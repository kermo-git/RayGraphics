package Graphics3D.Textures

import Graphics3D.Colors._
import Graphics3D.Components.NoiseFunction
import Graphics3D.GeometryUtils.Vec3
import Graphics3D.Textures.NoiseGenerator.lerp

object TextureFunctions {
  def colorBands(noise: NoiseFunction)(colors: Color*)(point: Vec3): Color =
    colors((noise(point) * colors.length).toInt)

  def smoothColorBands(noise: NoiseFunction)(colors: Color*)(point: Vec3): Color = {
    val numBands = colors.length - 1
    val noiseValue = noise(point) * numBands
    val colorIndex: Int = noiseValue.toInt
    val ratio = noiseValue - colorIndex

    blendColors(colors(colorIndex), colors(colorIndex + 1), ratio)
  }

  def wood(noise: NoiseFunction)(density: Int, stretchY: Double)(darkColor: Color, lightColor: Color)(point: Vec3): Color = {
    val noiseValue = noise(Vec3(point.x, point.y * stretchY, point.z)) * density
    val ratio = noiseValue - noiseValue.toInt
    blendColors(darkColor, lightColor, ratio)
  }

  private def blendColors(color1: Color, color2: Color, ratio: Double) = new Color(
    lerp(ratio, color1.red, color2.red),
    lerp(ratio, color1.green, color2.green),
    lerp(ratio, color1.blue, color2.blue)
  )
}
