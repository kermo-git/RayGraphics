package Graphics3D.Textures

import Graphics3D.GeometryUtils.Vec3

import scala.util.Random

object NoiseUtils {
  val MASK: Int = 255

  def fade(t: Double): Double = t * t * t * (t * (t * 6 - 15) + 10)

  def lerp(t: Double, a: Double, b: Double): Double = a + t * (b - a)

  def floor(t: Double): Int = if (t >= 0) t.toInt else t.toInt - 1

  def getHash(x: Int, y: Int, z: Int): Int = hashTable(hashTable(x) + y) + z

  def perlin(grad: (Int, Double, Double, Double) => Double)(point: Vec3): Double = {
    val fx: Int = floor(point.x)
    val fy: Int = floor(point.y)
    val fz: Int = floor(point.z)

    val x0: Int = fx & MASK
    val y0: Int = fy & MASK
    val z0: Int = fz & MASK

    val x1: Int = (x0 + 1) & MASK
    val y1: Int = (y0 + 1) & MASK
    val z1: Int = (z0 + 1) & MASK

    val px: Double = point.x - fx
    val py: Double = point.y - fy
    val pz: Double = point.z - fz

    val a: Double = grad(getHash(x0, y0, z0), px, py, pz)
    val b: Double = grad(getHash(x1, y0, z0), px - 1, py, pz)
    val c: Double = grad(getHash(x0, y1, z0), px, py - 1, pz)
    val d: Double = grad(getHash(x1, y1, z0), px - 1, py - 1, pz)
    val e: Double = grad(getHash(x0, y0, z1), px, py, pz - 1)
    val f: Double = grad(getHash(x1, y0, z1), px - 1, py, pz - 1)
    val g: Double = grad(getHash(x0, y1, z1), px, py - 1, pz - 1)
    val h: Double = grad(getHash(x1, y1, z1), px - 1, py - 1, pz - 1)

    val Sx: Double = fade(px)
    val Sy: Double = fade(py)
    val Sz: Double = fade(pz)

    lerp(Sz,
      lerp(Sy,
        lerp(Sx, a, b),
        lerp(Sx, c, d)),
      lerp(Sy,
        lerp(Sx, e, f),
        lerp(Sx, g, h)
      )
    )
  }

  def signedValueNoise(point: Vec3): Double = {
    val fx: Int = floor(point.x)
    val fy: Int = floor(point.y)
    val fz: Int = floor(point.z)

    val x0: Int = fx & MASK
    val y0: Int = fy & MASK
    val z0: Int = fz & MASK

    val x1: Int = (x0 + 1) & MASK
    val y1: Int = (y0 + 1) & MASK
    val z1: Int = (z0 + 1) & MASK

    val px: Double = point.x - fx
    val py: Double = point.y - fy
    val pz: Double = point.z - fz

    val a: Double = valueTable(getHash(x0, y0, z0))
    val b: Double = valueTable(getHash(x1, y0, z0))
    val c: Double = valueTable(getHash(x0, y1, z0))
    val d: Double = valueTable(getHash(x1, y1, z0))
    val e: Double = valueTable(getHash(x0, y0, z1))
    val f: Double = valueTable(getHash(x1, y0, z1))
    val g: Double = valueTable(getHash(x0, y1, z1))
    val h: Double = valueTable(getHash(x1, y1, z1))

    val Sx: Double = fade(px)
    val Sy: Double = fade(py)
    val Sz: Double = fade(pz)

    lerp(Sz,
      lerp(Sy,
        lerp(Sx, a, b),
        lerp(Sx, c, d)),
      lerp(Sy,
        lerp(Sx, e, f),
        lerp(Sx, g, h)
      )
    )
  }

  def perlin1986grad(gridCornerHash: Int, x: Double, y: Double, z: Double): Double = {
    val gradient: Vec3 = perlin1986Gradients(gridCornerHash)
    (gradient.x * x) + (gradient.y * y) + (gradient.z * z)
  }

  def perlin2002grad(gridCornerHash: Int, x: Double, y: Double, z: Double): Double = gridCornerHash & 15 match {
    case 0 => x + y
    case 1 => -x + y
    case 2 => x - y
    case 3 => -x - y
    case 4 => x + z
    case 5 => -x + z
    case 6 => x - z
    case 7 => -x - z
    case 8 => y + z
    case 9 => -y + z
    case 10 => y - z
    case 11 => -y - z
    case 12 => x + y
    case 13 => -x + y
    case 14 => -y + z
    case 15 => -y - z
  }

  def randSigned: Double = Random.nextDouble() * 2 - 1

  lazy val hashTable: Array[Int] = {
    val hashes = Random.shuffle(for (i <- 0 to MASK) yield i)
    (hashes ++ hashes).toArray
  }

  lazy val perlin1986Gradients: Array[Vec3] = {
    val allVectors = for (_ <- 0 to 3 * MASK)
      yield Vec3(randSigned, randSigned, randSigned)

    val inUnitSphere = allVectors.filter(vec => vec.length <= 1)
    val unitVectors = inUnitSphere.map(vec => vec.normalize).slice(0, MASK + 1)

    (unitVectors ++ unitVectors).toArray
  }

  lazy val valueTable: Array[Double] = {
    val values: Seq[Double] = for (_ <- 0 to MASK) yield randSigned
    (values ++ values).toArray
  }
}
