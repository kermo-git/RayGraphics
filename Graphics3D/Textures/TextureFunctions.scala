package Graphics3D.Textures

import Graphics3D.Colors.Color
import Graphics3D.Components.NoiseFunction
import Graphics3D.GeometryUtils.Vec3

object TextureFunctions {
  def colorBands(noise: NoiseFunction)(frequency: Double)(colors: Color*)(point: Vec3): Color = {
    val noiseValue = noise(point * frequency)
    colors((noiseValue * colors.length).toInt)
  }
}
