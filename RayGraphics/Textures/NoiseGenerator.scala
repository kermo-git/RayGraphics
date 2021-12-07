package RayGraphics.Textures

import scala.util.Random

import RayGraphics.Geometry.{Vec3, lerp}

object NoiseGenerator {
  def signedPerlinNoise(point: Vec3): Double = {
    val fx: Int = floor(point.x)
    val fy: Int = floor(point.y)
    val fz: Int = floor(point.z)

    val px: Double = point.x - fx
    val py: Double = point.y - fy
    val pz: Double = point.z - fz

    val x0: Int = fx & MASK
    val y0: Int = fy & MASK
    val z0: Int = fz & MASK

    val x1: Int = (x0 + 1) & MASK
    val y1: Int = (y0 + 1) & MASK
    val z1: Int = (z0 + 1) & MASK

    val a: Double = grad(hash(x0, y0, z0), px,     py,     pz)
    val b: Double = grad(hash(x1, y0, z0), px - 1, py,     pz)
    val c: Double = grad(hash(x0, y1, z0), px,     py - 1, pz)
    val d: Double = grad(hash(x1, y1, z0), px - 1, py - 1, pz)
    val e: Double = grad(hash(x0, y0, z1), px,     py,     pz - 1)
    val f: Double = grad(hash(x1, y0, z1), px - 1, py,     pz - 1)
    val g: Double = grad(hash(x0, y1, z1), px,     py - 1, pz - 1)
    val h: Double = grad(hash(x1, y1, z1), px - 1, py - 1, pz - 1)

    val Sx: Double = fade(px)
    val Sy: Double = fade(py)
    val Sz: Double = fade(pz)

    val result = 1.1 * lerp(
      lerp(
        lerp(a, b, Sx),
        lerp(c, d, Sx), Sy
      ),
      lerp(
        lerp(e, f, Sx),
        lerp(g, h, Sx), Sy
      ), Sz
    )
    if (result > 1) 1 else if (result < -1) -1 else result
  }

  val MASK: Int = 255

  def fade(t: Double): Double = t * t * t * (t * (t * 6 - 15) + 10)

  def floor(t: Double): Int = if (t >= 0) t.toInt else t.toInt - 1

  def hash(x: Int, y: Int, z: Int): Int = hashTable(hashTable(hashTable(x) + y) + z)

  def grad(hash: Int, x: Double, y: Double, z: Double): Double = hash & 15 match {
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

  lazy val hashTable: Array[Int] = {
    val hashes = Random.shuffle(for (i <- 0 to MASK) yield i)
    (hashes ++ hashes).toArray
  }
}
