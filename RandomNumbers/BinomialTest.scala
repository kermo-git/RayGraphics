package RandomNumbers

import Generators._

object BinomialTest {
  val prng: PRNG = new XORShift32(4576)
  val numTests: Int = 100000
  val N: Int = 1000
  val P: Double = 0.5

  var results: Array[Double] = new Array[Double](N)
  val scaleRange: Double = 40

  def main(args: Array[String]): Unit = {
    val startTime = System.nanoTime

    for (_ <- 1 to numTests)
      results(prng.randBinomial(N, P)) += 1

    val duration: Double = System.nanoTime - startTime

    val maxResult: Double = results.max

    results = results.map(result =>
      scaleRange * result / maxResult
    )
    val maxBar = results.max.toInt

    for (n <- maxBar + 1 to 0 by -1) {
      var row = ""

      for (i <- 0 until N) {
        if (results(i) >= n)
          row += " X"
        else
          row += "  "
      }
      println(row)
    }
    println()
    println("Generating random numbers took " + duration / 1e9 + " seconds")
  }
}
