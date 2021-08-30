package Graphics3D

import scala.math.{cos, sin, sqrt, toRadians}

object Utils {
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

  class Transformation(degX: Double, degY: Double, degZ: Double, x: Double, y: Double, z: Double) {
    def this(x: Double, y: Double, z: Double) {
      this(0, 0, 0, x, y, z)
    }

    private val radX = toRadians(degX)
    private val radY = toRadians(degY)
    private val radZ = toRadians(degZ)

    val rotation: Matrix = rotateAroundX(radX) * rotateAroundY(radY) * rotateAroundZ(radZ)
    val rotationInverse: Matrix = rotateAroundZ(-radZ) * rotateAroundY(-radY) * rotateAroundX(-radX)
    val fullTransformation: Matrix = rotation * translate(x, y, z)
    val fullInverse: Matrix = translate(-x, -y, -z) * rotationInverse
  }

  val nullTransformation: Transformation = new Transformation(0, 0, 0)

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
}
