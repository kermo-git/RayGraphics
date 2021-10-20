package Graphics3D

import scala.util.Random
import scala.math.{cos, sin, sqrt, abs, toRadians, random}

object Geometry {
  type Matrix = Array[Array[Double]]

  val IDENTITY_MATRIX: Matrix = Array(
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

  def coordinateSystem(yAxis: Vec3): Matrix = {
    val xAxis = if (abs(yAxis.x) > abs(yAxis.y))
      Vec3(yAxis.z, 0, -yAxis.x).normalize
    else {
      Vec3(0, -yAxis.z, yAxis.y).normalize
    }
    val zAxis = xAxis cross yAxis

    Array(
      Array(xAxis.x, xAxis.y, xAxis.z, 0),
      Array(yAxis.x, yAxis.y, yAxis.z, 0),
      Array(zAxis.x, zAxis.y, zAxis.z, 0),
      Array(0,       0,       0,       1),
    )
  }

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

  class Transformation(degX: Double, degY: Double, degZ: Double, x: Double, y: Double, z: Double) {
    def this(x: Double, y: Double, z: Double) {
      this(0, 0, 0, x, y, z)
    }

    private val radX = toRadians(degX)
    private val radY = toRadians(degY)
    private val radZ = toRadians(degZ)

    val rotation: Matrix = rotateAroundX(radX) * rotateAroundY(radY) * rotateAroundZ(radZ)
    val rotationInverse: Matrix = rotateAroundZ(-radZ) * rotateAroundY(-radY) * rotateAroundX(-radX)
    val full: Matrix = rotation * translate(x, y, z)
    val fullInverse: Matrix = translate(-x, -y, -z) * rotationInverse
  }

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

  def fromSpherical(theta: Double, phi: Double): Vec3 = Vec3(
    cos(phi) * sin(theta),
    sin(phi) * sin(theta),
    cos(theta)
  )

  val ORIGIN: Vec3 = Vec3(0, 0, 0)
  val UNIT_X: Vec3 = Vec3(1, 0, 0)
  val UNIT_Y: Vec3 = Vec3(0, 1, 0)
  val UNIT_Z: Vec3 = Vec3(0, 0, 1)

  private lazy val UNIT_VECTORS: Seq[Vec3] = {
    def randSigned: Double = 2 * random() - 1
    def randVector: Vec3 = Vec3(randSigned, randSigned, randSigned)

    (for (_ <- 0 to 10000) yield randVector)
      .filter(vec => vec.length <= 1)
      .map(vec => vec.normalize)
  }

  def randUnitVector: Vec3 = UNIT_VECTORS(Random.nextInt(UNIT_VECTORS.length))

  def lerp(a: Double, b: Double, t: Double): Double = a + t * (b - a)

  def lerp(a: Vec3, b: Vec3, t: Double): Vec3 = Vec3(
    lerp(a.x, b.x, t),
    lerp(a.y, b.y, t),
    lerp(a.z, b.z, t)
  )

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
}
