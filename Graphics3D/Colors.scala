package Graphics3D

object Colors {
  class Color(val red: Double, val green: Double, val blue: Double) {
    def this(hex: Int) {
      this(
        ((hex >> 16) & 0xFF) / 255.0,
        ((hex >> 8) & 0xFF) / 255.0,
        (hex & 0xFF) / 255.0
      )
    }

    def +(v: Color): Color = new Color(red + v.red, green + v.green, blue + v.blue)
    def *(v: Color): Color = new Color(red * v.red, green * v.green, blue * v.blue)
    def *(s: Double): Color = new Color(s * red, s * green, s * blue)

    def toHex: Int = {
      val r = if (red > 1) 1 else red
      val g = if (green > 1) 1 else green
      val b = if (blue > 1) 1 else blue

      ((r * 255).toInt << 16) | ((g * 255).toInt << 8) | (b * 255).toInt
    }
  }

  val MEDIUM_VIOLET_RED: Color = new Color(0xC71585)
  val DEEP_PINK: Color = new Color(0xFF1493)
  val HOT_PINK: Color = new Color(0xFF69B4)

  val RED: Color = new Color(0xFF0000)
  val FIREBRICK: Color = new Color(0x8B0000)
  val SALMON: Color = new Color(0xFA8072)

  val ORANGE_RED: Color = new Color(0xFF4500)
  val ORANGE: Color = new Color(0xFFA500)

  val YELLOW: Color = new Color(0xFFFF00)
  val GOLD: Color = new Color(0xFFD700)

  val SADDLE_BROWN: Color = new Color(0x8B4513)
  val PERU: Color = new Color(0xCD853F)
  val TAN: Color = new Color(0xD2B48C)
  val WHEAT: Color = new Color(0xF5DEB3)

  val GREEN: Color = new Color(0x008000)
  val LIME: Color = new Color(0x00FF00)
  val LAWN_GREEN: Color = new Color(0x7CFC00)

  val DARK_CYAN: Color = new Color(0x008B8B)
  val TURQUOISE: Color = new Color(0x40E0D0)
  val CYAN: Color = new Color(0x00FFFF)
  val AQUAMARINE: Color = new Color(0x7FFFD4)

  val DARK_BLUE: Color = new Color(0x00008B)
  val MEDIUM_BLUE: Color = new Color(0x0000CD)
  val BLUE: Color = new Color(0x0000FF)
  val DEEP_SKY_BLUE: Color = new Color(0x00BFFF)
  val LIGHT_SKY_BLUE: Color = new Color(0x87CEFA)

  val INDIGO: Color = new Color(0x4B0082)
  val PURPLE: Color = new Color(0x800080)
  val DARK_VIOLET: Color = new Color(0x9400D3)
  val MAGENTA: Color = new Color(0xFF00FF)
  val MEDIUM_SLATE_BLUE: Color = new Color(0x7B68EE)

  val HONEYDEV: Color = new Color(0xF0FFF0)
  val AZURE: Color = new Color(0x0FFFFF)
  val IVORY: Color = new Color(0xFFFFF0)
  val WHITE: Color = new Color(0xFFFFFF)

  val BLACK: Color = new Color(0x000000)
  val GRAY: Color = new Color(0x808080)
  val SILVER: Color = new Color(0xC0C0C0)
  val LIGHT_GRAY: Color = new Color(0xD3D3D3)
}
