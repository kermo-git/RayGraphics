import scala.math.{cos, sin, sqrt, toRadians}

package object Graphics3D {
  type Matrix = Array[Array[Double]]

  val identMat: Matrix = Array(
    Array(1, 0, 0, 0),
    Array(0, 1, 0, 0),
    Array(0, 0, 1, 0),
    Array(0, 0, 0, 1),
  )

  def rotateAroundX(rad: Double): Matrix = Array(
    Array(1,  0,        0,        0),
    Array(0,  cos(rad), sin(rad), 0),
    Array(0, -sin(rad), cos(rad), 0),
    Array(0,  0,        0,        1),
  )

  def rotateAroundY(rad: Double): Matrix = Array(
    Array(cos(rad), 0, -sin(rad), 0),
    Array(0,        1,  0,        0),
    Array(sin(rad), 0,  cos(rad), 0),
    Array(0,        0,  0,        1),
  )

  def rotateAroundZ(rad: Double): Matrix = Array(
    Array( cos(rad), sin(rad), 0, 0),
    Array(-sin(rad), cos(rad), 0, 0),
    Array( 0,        0,        1, 0),
    Array( 0,        0,        0, 1),
  )

  def translate(x: Double, y: Double, z: Double): Matrix = Array(
    Array(1, 0, 0, 0),
    Array(0, 1, 0, 0),
    Array(0, 0, 1, 0),
    Array(x, y, z, 1),
  )

  implicit class MatrixOps(mat: Matrix) {
    def *(other: Matrix): Matrix = {
      val rows = mat.indices
      val columns = other(0).indices
      val common = other.indices

      rows.map(row =>
        columns.map(col =>
          common.foldLeft(0.0)(
            (sum, i) => sum + mat(row)(i) * other(i)(col)
          )
        ).toArray
      ).toArray
    }
  }

  class Position(degX: Double, degY: Double, degZ: Double, x: Double, y: Double, z: Double) {
    def this(x: Double, y: Double, z: Double) {
      this(0, 0, 0, x, y, z)
    }

    private val radX = toRadians(degX)
    private val radY = toRadians(degY)
    private val radZ = toRadians(degZ)

    val rot: Matrix = rotateAroundX(radX) * rotateAroundY(radY) * rotateAroundZ(radZ)
    val rotInv: Matrix = rotateAroundZ(-radZ) * rotateAroundY(-radY) * rotateAroundX(-radX)
    val full: Matrix = rot * translate(x, y, z)
    val fullInv: Matrix = translate(-x, -y, -z) * rotInv
  }

  val noMove: Position = new Position(0, 0, 0)

  case class Vec3(x: Double, y: Double, z: Double) {
    def this(v1: Vec3, v2: Vec3) {
      this(v2.x - v1.x, v2.y - v1.y, v2.z - v1.z)
    }

    def +(v: Vec3): Vec3 = Vec3(x + v.x, y + v.y, z + v.z)
    def -(v: Vec3): Vec3 = Vec3(x - v.x, y - v.y, z - v.z)
    def *(s: Double): Vec3 = Vec3(s * x, s * y, s * z)
    def *(v: Vec3): Vec3 = Vec3(x * v.x, y * v.y, z * v.z)
    def *(m: Matrix): Vec3 = Vec3(
      x * m(0)(0) + y * m(1)(0) + z * m(2)(0) + m(3)(0),
      x * m(0)(1) + y * m(1)(1) + z * m(2)(1) + m(3)(1),
      x * m(0)(2) + y * m(1)(2) + z * m(2)(2) + m(3)(2)
    )

    def dot(v: Vec3): Double = x * v.x + y * v.y + z * v.z

    def cross(v: Vec3): Vec3 = Vec3(
      y * v.z - z * v.y,
      z * v.x - x * v.z,
      x * v.y - y * v.x
    )

    lazy val length: Double = sqrt(x * x + y * y + z * z)

    def normalize: Vec3 = Vec3(x / length, y / length, z / length)

    def invert: Vec3 = Vec3(-x, -y, -z)
  }

  val origin: Vec3 = Vec3(0, 0, 0)
  val unitX: Vec3 = Vec3(1, 0, 0)
  val unitY: Vec3 = Vec3(0, 1, 0)
  val unitZ: Vec3 = Vec3(0, 0, 1)

  def solveQuadraticEquation(a: Double, b: Double, c: Double): Option[(Double, Double)] = {
    val D = b * b - 4 * a * c

    if (D < 0)
      None
    else {
      val sqrtD = sqrt(D)
      val doubleA = 2 * a
      Some((-b - sqrtD) / doubleA, (-b + sqrtD) / doubleA)
    }
  }

  def reflection(incident: Vec3, normal: Vec3): Vec3 = incident - normal * (2 * (incident dot normal))

  def schlick(n1: Double, n2: Double, cos: Double): Double = {
    val R0 = (n1 - n2) / (n1 + n2)
    val _cos = 1 - cos
    R0 * R0 + (1 - R0 * R0) * _cos * _cos * _cos * _cos * _cos
  }

  def refraction(incident: Vec3, normal: Vec3, n1: Double, n2: Double): Option[Vec3] = {
    val n = n1 / n2
    val cos_1 = -(incident dot normal)
    val cos_2_sqr = 1 - n * n * (1 - cos_1 * cos_1)

    if (cos_2_sqr < 0)
      None
    else {
      val cos_2 = sqrt(cos_2_sqr)
      Some((normal * cos_1 + incident) * n - normal * cos_2)
    }
  }

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

  val BLACK: Color = new Color(0x000000)
  val GRAY: Color = new Color(0x808080)
  val SILVER: Color = new Color(0xC0C0C0)
  val LIGHT_GRAY: Color = new Color(0xD3D3D3)

  val WHITE: Color = new Color(0xFFFFFF)
  val IVORY: Color = new Color(0xFFFFF0)
  val HONEYDEV: Color = new Color(0xF0FFF0)
  val AZURE: Color = new Color(0x0FFFFF)

  val RED: Color = new Color(0xFF0000)
  val FIREBRICK: Color = new Color(0x8B0000)
  val SALMON: Color = new Color(0xFA8072)

  val HOT_PINK: Color = new Color(0xFF69B4)
  val DEEP_PINK: Color = new Color(0xFF1493)
  val MEDIUM_VIOLET_RED: Color = new Color(0xC71585)

  val YELLOW: Color = new Color(0xFFFF00)
  val GOLD: Color = new Color(0xFFD700)
  val ORANGE: Color = new Color(0xFFA500)
  val ORANGE_RED: Color = new Color(0xFF4500)

  val GREEN: Color = new Color(0x008000)
  val LAWN_GREEN: Color = new Color(0x7CFC00)
  val LIME: Color = new Color(0x00FF00)

  val DARK_CYAN: Color = new Color(0x008B8B)
  val TURQUOISE: Color = new Color(0x40E0D0)
  val CYAN: Color = new Color(0x00FFFF)
  val AQUAMARINE: Color = new Color(0x7FFFD4)

  val DARK_BLUE: Color = new Color(0x00008B)
  val BLUE: Color = new Color(0x0000FF)
  val MEDIUM_BLUE: Color = new Color(0x0000CD)
  val DEEP_SKY_BLUE: Color = new Color(0x00BFFF)
  val LIGHT_SKY_BLUE: Color = new Color(0x87CEFA)

  val INDIGO: Color = new Color(0x4B0082)
  val PURPLE: Color = new Color(0x800080)
  val DARK_VIOLET: Color = new Color(0x9400D3)
  val MAGENTA: Color = new Color(0xFF00FF)
  val MEDIUM_SLATE_BLUE: Color = new Color(0x7B68EE)
}
