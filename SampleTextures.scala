import RayGraphics._
import RayGraphics.Color
import ColorUtils.{createLinearColor, lerp}
import Geometry.Vec3
import LinearColors._
import Textures.Components.Texture
import Textures.TextureUtils._

object SampleTextures {
  val LAVA_ROCK: Texture =
    smoothColorBands(octaveNoise(absPerlinNoise)())(ORANGE_RED, BLACK, GRAY, GRAY, GRAY)

  val EXPLOSION: Texture =
    smoothColorBands(octaveNoise(absPerlinNoise)())(BLACK, ORANGE_RED, YELLOW, YELLOW, YELLOW)

  val CLOUDS: Texture =
    smoothColorBands(octaveNoise(absPerlinNoise)(octaves = 8, persistence = 0.5))(DEEP_SKY_BLUE, WHITE, WHITE)

  val GREEN_LEAVES: Texture =
    smoothColorBands(octaveNoise(absPerlinNoise)(octaves = 4, persistence = 0.6))(BLACK, DARK_GREEN, DARK_GREEN)

  val SEAGREEN_LEAVES: Texture =
    smoothColorBands(octaveNoise(absPerlinNoise)(octaves = 4, persistence = 0.6))(BLACK, SEAGREEN, SEAGREEN)

  val OLIVE_LEAVES: Texture =
    smoothColorBands(octaveNoise(absPerlinNoise)(octaves = 4, persistence = 0.6))(BLACK, OLIVE_DRAB, OLIVE_DRAB)

  def BROWN_TREE_BARK(point: Vec3): Color = {
    val noise = perlinNoise(point * Vec3(10, 1, 10))
    lerp(createLinearColor(0x916A36), BLACK, noise)
  }

  def GRAY_TREE_BARK(point: Vec3): Color = {
    val noise = perlinNoise(point * Vec3(10, 1, 10))
    lerp(createLinearColor(0x80725c), BLACK, noise)
  }

  val DARK_WOOD: Texture =
    wood(
      noise = absPerlinNoise, density = 10, stretchY = 0.2,
      darkColor = createLinearColor(0x2E1E00),
      lightColor = createLinearColor(0x6B4020)
    )(_: Vec3)

  val MEDIUM_WOOD: Texture =
    wood(
      noise = absPerlinNoise, density = 10, stretchY = 0.2,
      darkColor = createLinearColor(0x916A36),
      lightColor = createLinearColor(0xD49A4E)
    )(_: Vec3)

  val LIGHT_WOOD: Texture =
    wood(
      noise = absPerlinNoise, density = 10, stretchY = 0.2,
      darkColor = createLinearColor(0xD6AB72),
      lightColor = createLinearColor(0xF5D7B3)
    )(_: Vec3)
}
