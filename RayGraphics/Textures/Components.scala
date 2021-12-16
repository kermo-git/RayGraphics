package RayGraphics.Textures

import RayGraphics.Geometry.Vec3
import RayGraphics.Color
import RayGraphics.Components.Renderable

object Components {
  type Noise = Vec3 => Double
  type Texture = Vec3 => Color

  class NoiseDisplay(val imageWidth: Int,
                     val imageHeight: Int,
                     val unitSizePx: Int,
                     val noiseZ: Double = 0,
                     val noise: Noise) extends Renderable {

    override def getPixelColor(x: Int, y: Int): Color = {
      val noiseX = 1.0 * x / unitSizePx
      val noiseY = 1.0 * y / unitSizePx
      val noiseValue = noise(Vec3(noiseX, noiseY, noiseZ))
      new Color(noiseValue, noiseValue, noiseValue)
    }
  }

  class TextureDisplay(val imageWidth: Int,
                       val imageHeight: Int,
                       val unitSizePx: Int,
                       val textureZ: Double = 0,
                       val texture: Texture) extends Renderable {

    override def getPixelColor(x: Int, y: Int): Color = {
      val textureX = 1.0 * x / unitSizePx
      val textureY = 1.0 * y / unitSizePx
      texture(Vec3(textureX, textureY, textureZ))
    }
  }
}
