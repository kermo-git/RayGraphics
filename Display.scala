import java.awt.image.BufferedImage
import java.awt.Graphics
import javax.swing.JFrame
import javax.swing.JPanel

import Graphics3D.GeometryUtils.Vec3
import Graphics3D.Components._

import scala.annotation.tailrec

class Display(val image: BufferedImage) extends JPanel {
  override def paint(g: Graphics): Unit = {
    g.drawImage(image, 0, 0, this)
  }
}

object Main {
  def renderImage(renderable: Renderable): BufferedImage = {
    val image: BufferedImage = new BufferedImage(
      renderable.imageWidth,
      renderable.imageHeight,
      BufferedImage.TYPE_INT_RGB
    )
    for (x <- 0 until renderable.imageWidth) {
      for (y <- 0 until renderable.imageHeight) {
        val color = renderable.getPixelColor(x, y)
        image.setRGB(x, y, color.toHex)
      }
    }
    image
  }

  def measureNoise(noise: NoiseFunction, units: Int, noiseZ: Double, increment: Double): Unit = {
    @tailrec
    def loop(x: Double = 0.0, y: Double = 0.0, min: Double = 10.0, max: Double = -10.0): Unit = {
      val noiseValue = noise(Vec3(x, y, noiseZ))

      val nextMin = if (noiseValue < min) noiseValue else min
      val nextMax = if (noiseValue > max) noiseValue else max

      val (nextX, nextY): (Double, Double) =
        if (x >= units)
          (0.0, y + increment)
        else
          (x + increment, y)

      if (nextY <= units)
        loop(nextX, nextY, nextMin, nextMax)
      else {
        println("Min noise value: " + nextMin)
        println("Max noise value: " + nextMax)
      }
    }
    loop()
  }

  def main(args: Array[String]): Unit = {
    val startTime = System.nanoTime

    val image: BufferedImage = renderImage(Scenes.testScene)
    val frame = new JFrame
    frame.setTitle("3D Graphics")
    frame.setSize(image.getWidth, image.getHeight)
    frame.add(new Display(image))
    frame.setVisible(true)

    val duration: Double = System.nanoTime - startTime
    println("Rendering took " + duration / 1e9 + " seconds")
  }
}
