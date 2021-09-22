package RandomNumbers

import Generators._

object NormalTest {
  val prng: PRNG = new XORShift32(628134)
  val numTests: Int = 100000

  val numBins: Int = 50
  val minBound: Double = -3
  val maxBound: Double = 3
  val range: Double = maxBound - minBound

  val inverseCDF: Double => Double =
    getInverseCDF(standardNormalPDF, numBins)
  val centralLimitGenerator = new CentralLimitGenerator(prng, 100)

  var results: Array[Double] = new Array[Double](numBins)
  val scaleRange: Double = 20

  def main(args: Array[String]): Unit = {
    val startTime = System.nanoTime

    for (_ <- 1 to numTests) {
      val normalValue = inverseCDF(prng.randUniform)
      // val normalValue = centralLimitGenerator.randStandardNormal

      if (normalValue >= minBound && normalValue < maxBound) {
        val index = ((normalValue - minBound) * numBins / range).toInt
        results(index) += 1
      }
    }
    val duration: Double = System.nanoTime - startTime

    val maxResult: Double = results.max
    results = results.map(result =>
      scaleRange * result / maxResult
    )
    val maxBar = results.max.toInt

    for (n <- maxBar + 1 to 0 by -1) {
      var row = ""

      for (i <- 0 until numBins) {
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
