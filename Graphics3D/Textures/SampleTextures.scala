package Graphics3D.Textures

import Graphics3D._
import Geometry.Vec3
import Graphics3D.Color
import ColorUtils._
import LinearColors._
import Textures.Components.TextureFunction
import Textures.TextureUtils._

object SampleTextures {
  val LIGHT_BLUE_SPOTS: TextureFunction =
    smoothColorBands(perlinNoise)(DARK_BLUE, MEDIUM_BLUE, DEEP_SKY_BLUE, MEDIUM_BLUE)

  val PINK_BLUE_TEXTURE: TextureFunction =
    smoothColorBands(perlinNoise)(BLACK, HOT_PINK, BLACK, DEEP_SKY_BLUE, BLACK)

  val FIRE: TextureFunction =
    smoothColorBands(octaveNoise(perlinNoise)(octaves = 6, persistence = 0.6))(YELLOW, GOLD, ORANGE_RED, RED)

  val LAVA_ROCK: TextureFunction =
    smoothColorBands(octaveNoise(absPerlinNoise)())(ORANGE_RED, BLACK, GRAY, GRAY, GRAY)

  val EXPLOSION: TextureFunction =
    smoothColorBands(octaveNoise(absPerlinNoise)())(BLACK, ORANGE_RED, YELLOW, YELLOW, YELLOW)

  val CLOUDS: TextureFunction =
    smoothColorBands(octaveNoise(absPerlinNoise)(octaves = 8, persistence = 0.5))(DEEP_SKY_BLUE, WHITE, WHITE)

  val GREEN_LEAVES: TextureFunction =
    smoothColorBands(octaveNoise(absPerlinNoise)(octaves = 4, persistence = 0.6))(BLACK, DARK_GREEN, DARK_GREEN)

  val SEAGREEN_LEAVES: TextureFunction =
    smoothColorBands(octaveNoise(absPerlinNoise)(octaves = 4, persistence = 0.6))(BLACK, SEAGREEN, SEAGREEN)

  val OLIVE_LEAVES: TextureFunction =
    smoothColorBands(octaveNoise(absPerlinNoise)(octaves = 4, persistence = 0.6))(BLACK, OLIVE_DRAB, OLIVE_DRAB)

  def BROWN_TREE_BARK(point: Vec3): Color = {
    val noise = perlinNoise(point * Vec3(10, 1, 10))
    lerp(createLinearColor(0x916A36), BLACK, noise)
  }

  def GRAY_TREE_BARK(point: Vec3): Color = {
    val noise = perlinNoise(point * Vec3(10, 1, 10))
    lerp(createLinearColor(0x80725c), BLACK, noise)
  }

  val DARK_WOOD: TextureFunction =
    wood(
      noise = absPerlinNoise, density = 10, stretchY = 0.2,
      darkColor = createLinearColor(0x2E1E00),
      lightColor = createLinearColor(0x6B4020)
    )(_: Vec3)

  val MEDIUM_WOOD: TextureFunction =
    wood(
      noise = absPerlinNoise, density = 10, stretchY = 0.2,
      darkColor = createLinearColor(0x916A36),
      lightColor = createLinearColor(0xD49A4E)
    )(_: Vec3)

  val LIGHT_WOOD: TextureFunction =
    wood(
      noise = absPerlinNoise, density = 10, stretchY = 0.2,
      darkColor = createLinearColor(0xD6AB72),
      lightColor = createLinearColor(0xF5D7B3)
    )(_: Vec3)
}
