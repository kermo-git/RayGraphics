package Graphics3D

import scala.math.pow

class Color(val red: Double, val green: Double, val blue: Double) {
  def +(v: Color): Color = new Color(red + v.red, green + v.green, blue + v.blue)
  def *(v: Color): Color = new Color(red * v.red, green * v.green, blue * v.blue)
  def *(s: Double): Color = new Color(s * red, s * green, s * blue)

  def invert: Color = new Color(1.0 - red, 1.0 - green, 1.0 - blue)
  def apply(f: Double => Double): Color = new Color(f(red), f(green), f(blue))
  def toHex: Int = ((red * 255).toInt << 16) | ((green * 255).toInt << 8) | (blue * 255).toInt
}

object ColorUtils {
  def exposureToneMap(color: Double, exposure: Double = 1): Double = 1 - math.exp(-color * exposure)
  def reinhardToneMap(color: Double): Double = color / (color + 1.0)

  // http://entropymine.com/imageworsener/srgbformula/

  def toLinear(sRGB: Double): Double =
    if (sRGB <= 0.04045)
      sRGB / 12.92
    else
      pow((sRGB + 0.055) / 1.055, 2.4)

  def tosRGB(linear: Double): Double =
    if (linear <= 0.0031308)
      linear * 12.92
    else
      pow(1.055 * linear, 1 / 2.4) - 0.055

  def createColor(red: Int, green: Int, blue: Int): Color = new Color(red / 255.0, green / 255.0, blue / 255.0)
  def colorFromHex(value: Int): Color = createColor(
    (value >> 16) & 0xFF,
    (value >> 8) & 0xFF,
    value & 0xFF
  )

  def createLinearColor(sRed: Int, sGreen: Int, sBlue: Int): Color = createColor(sRed, sGreen, sBlue) apply toLinear
  def createLinearColor(sRGB: Int): Color = colorFromHex(sRGB) apply toLinear

  def lerp(a: Color, b: Color, ratio: Double): Color = new Color(
    Geometry.lerp(a.red, b.red, ratio),
    Geometry.lerp(a.green, b.green, ratio),
    Geometry.lerp(a.blue, b.blue, ratio)
  )
}

object LinearColors {
  import ColorUtils.createLinearColor

  val MEDIUM_VIOLET_RED: Color = createLinearColor(0xC71585)
  val DEEP_PINK: Color = createLinearColor(0xFF1493)
  val HOT_PINK: Color = createLinearColor(0xFF69B4)

  val RED: Color = createLinearColor(0xFF0000)
  val FIREBRICK: Color = createLinearColor(0x8B0000)
  val SALMON: Color = createLinearColor(0xFA8072)

  val ORANGE_RED: Color = createLinearColor(0xFF4500)
  val ORANGE: Color = createLinearColor(0xFFA500)

  val YELLOW: Color = createLinearColor(0xFFFF00)
  val GOLD: Color = createLinearColor(0xFFD700)

  val SADDLE_BROWN: Color = createLinearColor(0x8B4513)
  val PERU: Color = createLinearColor(0xCD853F)
  val TAN: Color = createLinearColor(0xD2B48C)
  val WHEAT: Color = createLinearColor(0xF5DEB3)

  val DARK_GREEN: Color = createLinearColor(0x006400)
  val GREEN: Color = createLinearColor(0x008000)
  val SEAGREEN: Color = createLinearColor(0x2E8B57)
  val OLIVE_DRAB: Color = createLinearColor(0x6B8E23)
  val LIME: Color = createLinearColor(0x00FF00)
  val LAWN_GREEN: Color = createLinearColor(0x7CFC00)

  val DARK_CYAN: Color = createLinearColor(0x008B8B)
  val TURQUOISE: Color = createLinearColor(0x40E0D0)
  val CYAN: Color = createLinearColor(0x00FFFF)
  val AQUAMARINE: Color = createLinearColor(0x7FFFD4)

  val DARK_BLUE: Color = createLinearColor(0x00008B)
  val MEDIUM_BLUE: Color = createLinearColor(0x0000CD)
  val BLUE: Color = createLinearColor(0x0000FF)
  val DEEP_SKY_BLUE: Color = createLinearColor(0x00BFFF)
  val LIGHT_SKY_BLUE: Color = createLinearColor(0x87CEFA)

  val INDIGO: Color = createLinearColor(0x4B0082)
  val PURPLE: Color = createLinearColor(0x800080)
  val DARK_VIOLET: Color = createLinearColor(0x9400D3)
  val MAGENTA: Color = createLinearColor(0xFF00FF)
  val MEDIUM_SLATE_BLUE: Color = createLinearColor(0x7B68EE)

  val HONEYDEV: Color = createLinearColor(0xF0FFF0)
  val AZURE: Color = createLinearColor(0x0FFFFF)
  val IVORY: Color = createLinearColor(0xFFFFF0)
  val WHITE: Color = new Color(1, 1, 1)

  val BLACK: Color = new Color(0, 0, 0)
  val GRAY: Color = createLinearColor(0x808080)
  val SILVER: Color = createLinearColor(0xC0C0C0)
  val LIGHT_GRAY: Color = createLinearColor(0xD3D3D3)
}
