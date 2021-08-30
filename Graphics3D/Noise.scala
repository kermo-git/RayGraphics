package Graphics3D

import Graphics3D.BaseObjects.Renderable
import Graphics3D.Colors.Color
import Utils._

import scala.annotation.tailrec
import scala.math.abs
import scala.util.Random

object Noise {
  type NoiseFunction = Vec3 => Double

  class NoiseDisplay(
    val imageWidth: Int,
    val imageHeight: Int,
    val unitSizePx: Int,
    val noise: NoiseFunction
  ) extends Renderable {

    override def getPixelColor(x: Int, y: Int): Color = {
      val noiseX = 1.0 * x / unitSizePx
      val noiseY = 1.0 * y / unitSizePx
      val noiseValue = abs(noise(Vec3(noiseX, noiseY, 0)))
      new Color(noiseValue, noiseValue, noiseValue)
    }
  }

  def perlinNoise(point: Vec3): Double = 0.5 * signedPerlinNoise(point) + 0.5
  def turbulencePerlinNoise(point: Vec3): Double = abs(signedPerlinNoise(point))

  def valueNoise(point: Vec3): Double = 0.5 * signedValueNoise(point) + 0.5
  def turbulenceValueNoise(point: Vec3): Double = abs(signedValueNoise(point))

  def fractalNoise(noiseFunction: NoiseFunction)(octaves: Int = 4, persistence: Double = 0.4)(point: Vec3): Double = {
    @tailrec
    def octave(i: Int = 1, total: Double = 0, frequency: Double = 1, amplitude: Double = 1, max_value: Double = 0): Double = {
      if (i == octaves)
        total / max_value
      else
        octave(
          i = i + 1,
          total = total + amplitude * noiseFunction(point * frequency),
          max_value = max_value + amplitude,
          amplitude = amplitude * persistence,
          frequency = 2 * frequency
        )
    }
    octave()
  }

  private def duplicate[T](list: Seq[T]): Seq[T] = for (i <- 0 until 2 * list.length) yield list(i % (list.length - 1))

  private val MASK: Int = 255

  private lazy val hashTable: Array[Int] = duplicate(Random.shuffle(for (i <- 0 to MASK) yield i)).toArray

  private lazy val valueTable: Array[Double] = duplicate(for (_ <- 0 to MASK) yield {
    val value = Random.nextDouble()
    val sign = if (Random.nextBoolean()) 1 else -1
    value * sign
  }).toArray

  private def fade(t: Double): Double = t * t * t * (t * (t * 6 - 15) + 10)

  private def lerp(t: Double, a: Double, b: Double): Double = a + t * (b - a)

  private def floor(t: Double): Int = if (t >= 0) t.toInt else t.toInt - 1

  private def hash(x: Int, y: Int, z: Int): Int = hashTable(hashTable(x) + y) + z

  private def value(x: Int, y: Int, z: Int): Double = valueTable(hashTable(hashTable(x) + y) + z)

  private def grad(hash: Int, x: Double, y: Double, z: Double): Double = hash & 15 match {
    case 0 =>   x + y
    case 1 =>  -x + y
    case 2 =>   x - y
    case 3 =>  -x - y
    case 4 =>   x + z
    case 5 =>  -x + z
    case 6 =>   x - z
    case 7 =>  -x - z
    case 8 =>   y + z
    case 9 =>  -y + z
    case 10 =>  y - z
    case 11 => -y - z
    case 12 =>  x + y
    case 13 => -x + y
    case 14 => -y + z
    case 15 => -y - z
  }

  private def signedPerlinNoise(point: Vec3): Double  = {
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

  private def signedValueNoise(point: Vec3): Double  = {
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

    val a: Double = value(x0, y0, z0)
    val b: Double = value(x1, y0, z0)
    val c: Double = value(x0, y1, z0)
    val d: Double = value(x1, y1, z0)
    val e: Double = value(x0, y0, z1)
    val f: Double = value(x1, y0, z1)
    val g: Double = value(x0, y1, z1)
    val h: Double = value(x1, y1, z1)

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
}