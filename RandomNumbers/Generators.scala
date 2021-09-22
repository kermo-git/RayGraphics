package RandomNumbers

import scala.annotation.tailrec
import scala.math.{Pi, exp, sqrt}

object Generators {
  def getInverseCDF(PDF: Double => Double,
                    numPoints: Int = 32,
                    minBound: Double = -5.0,
                    maxBound: Double = 5.0): Double => Double = {

    val dx = (maxBound - minBound) / numPoints

    def xValue(index: Int): Double = minBound + index * dx

    def getCDFValues(xIndex: Int, prevValue: Double): List[Double] =
      if (xIndex >= numPoints) 1 :: Nil else {
        val x = xValue(xIndex)
        val nextValue = prevValue + PDF(x) * dx
        nextValue :: getCDFValues(xIndex + 1, nextValue)
      }

    val CDFValues: List[Double] = 0 :: getCDFValues(1, 0)

    @tailrec
    def getInverseCDFValue(uniformValue: Double, xIndex: Int, CDFValues: List[Double]): Double =
      CDFValues match {
        case value1 :: value2 :: tail =>
          if (uniformValue < value2) {
            val xValue1 = xValue(xIndex)
            val xValue2 = xValue(xIndex + 1)
            val t = (uniformValue - value1) / (value2 - value1)
            xValue1 + t * (xValue2 - xValue1)
          }
          else
            getInverseCDFValue(uniformValue, xIndex + 1, value2 :: tail)
        case _ => xValue(xIndex)
      }

    (uniformValue: Double) => getInverseCDFValue(uniformValue, 0, CDFValues)
  }

  def standardNormalPDF(x: Double): Double = exp(-(x * x) / 2) / sqrt(2 * Pi)

  val uniformToNormal: Double => Double = getInverseCDF(standardNormalPDF)

  class CentralLimitGenerator(prng: PRNG, numSamples: Int) {
    private val mean = numSamples * 0.5
    private val standardDeviation = sqrt(numSamples / 12)

    def randStandardNormal: Double = {
      val uniformValues = for (_ <- 1 to numSamples) yield prng.randUniform
      (uniformValues.sum - mean) / standardDeviation
    }

    def randNormal(mean: Double, stDev: Double): Double = randStandardNormal * stDev + mean
  }

  trait PRNG {
    val maxVal: Int

    def randInt: Int

    def randUniform: Double = 1.0 * randInt / maxVal

    def randUniform(a: Double, b: Double): Double = randUniform * (b - a) + a

    def randInt(a: Int, b: Int): Int = randUniform(a, b + 1).toInt

    def randBoolean(p: Double = 0.5): Boolean = randUniform < p

    def randBinomial(n: Int, p: Double): Int = {
      var count: Int = 0

      for (_ <- 1 to n) {
        count = count + (if (randUniform < p) 1 else 0)
      }
      count
    }
  }

  class LCG(val seed: Int,
            val a: Int,
            val c: Int,
            override val maxVal: Int) extends PRNG {

    private var state = seed

    override def randInt: Int = {
      state = (a * state + c) % maxVal
      state
    }
  }

  // https://en.wikipedia.org/wiki/Linear_congruential_generator
  def ZX81(seed: Int) = new LCG(seed, 75, 74, (1 << 16) + 1)

  def MS_VISUAL_BASIC(seed: Int) = new LCG(seed, 1140671485, 12820163, 1 << 24)

  def MINSTD0(seed: Int) = new LCG(seed, 16807, 0, (1 << 31) - 1)

  def MINSTD(seed: Int) = new LCG(seed, 48271, 0, (1 << 31) - 1)

  // https://dl.acm.org/doi/pdf/10.5555/2955239.2955463
  def ANOTHER_LCG(seed: Int) = new LCG(seed, 14359, 9654, 21569)

  class XORShift32(val seed: Int) extends PRNG {
    override val maxVal: Int = (1 << 31) - 1

    private var state = seed

    override def randInt: Int = {
      state ^= state << 13
      state ^= state >> 17
      state ^= state << 5
      if (state < 0) -state else state
    }
  }

  class XORShift128(val seed: Int) extends PRNG {
    override val maxVal: Int = (1 << 31) - 1

    private val initializer = new XORShift32(seed)

    private var state0 = initializer.randInt
    private var state1 = initializer.randInt
    private var state2 = initializer.randInt
    private var state3 = initializer.randInt

    override def randInt: Int = {
      var t = state3
      val s = state0

      state3 = state2
      state2 = state1
      state1 = s

      t ^= t << 11
      t ^= t >> 8

      state0 = t ^ s ^ (s >> 19)
      if (state0 < 0) -state0 else state0
    }
  }

  class RANLUX(val seed: Int = 314159265,
               override val maxVal: Int = 1 << 24,
               val s: Int = 10,
               var r: Int = 24,
               val luxury: Int = 223) extends PRNG {

    override def randInt: Int = {
      if (outputCount == r) {
        for (_ <- 1 to luxury - r) {
          advanceState
        }
        outputCount = 0
      }
      outputCount += 1
      advanceState
    }

    private val state: Array[Int] = new Array(r)

    private var indexS: Int = s - 1
    private var indexR: Int = r - 1
    private var carry: Int = 0
    private var outputCount: Int = 0

    def reset(): Unit = {
      var s: Int = seed

      for (i <- 0 until r) {
        val k: Int = s / 53668
        s = 40014 * (s - k * 53668) - k * 12211
        if (s < 0) {
          s += 2147483563
        }
        state(i) = s % maxVal
      }
      carry = if (state(r - 1) < 0) 1 else 0
    }

    reset()

    private def advanceState: Int = {
      var nextRand = state(indexS) - state(indexR) - carry

      if (nextRand < 0) {
        carry = 1
        nextRand += maxVal
      } else {
        carry = 0
      }
      state(indexR) = nextRand
      indexS = if (indexS == 0) 23 else indexS - 1
      indexR = if (indexR == 0) 23 else indexR - 1

      nextRand
    }
  }
}
