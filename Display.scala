import java.awt.image.BufferedImage
import java.awt.Graphics
import javax.swing.JFrame
import javax.swing.JPanel

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

import Graphics3D.Components._

class Display(val image: BufferedImage) extends JPanel {
  override def paint(g: Graphics): Unit = {
    g.drawImage(image, 0, 0, this)
  }
}

object Main {
  def renderImage(renderable: Renderable): Future[BufferedImage] = {
    val image: BufferedImage = new BufferedImage(
      renderable.imageWidth,
      renderable.imageHeight,
      BufferedImage.TYPE_INT_RGB
    )
    case class Pixel(x: Int, y: Int, color: Int)

    def paintPixel(x: Int, y: Int): Future[Pixel] = Future {
      Pixel(x, y, renderable.getPixelColor(x, y).toInt)
    }
    val pixels = for {
      x <- 0 until renderable.imageWidth
      y <- 0 until renderable.imageHeight
    } yield paintPixel(x, y)

    Future.sequence(pixels).flatMap((pixelList: Seq[Pixel]) => {
      pixelList.foreach {
        case Pixel(x, y, color) => image.setRGB(x, y, color)
      }
      Future(image)
    })
  }

  def main(args: Array[String]): Unit = {
    val startTime = System.nanoTime

    renderImage(Scenes.cornellBox).onComplete {
      case Success(image) =>
        val duration: Double = System.nanoTime - startTime
        println("Rendering took " + duration / 1e9 + " seconds")

        val frame = new JFrame
        frame.setTitle("3D Graphics")
        frame.setSize(image.getWidth, image.getHeight)
        frame.add(new Display(image))
        frame.setVisible(true)

      case Failure(e) => e.printStackTrace()
    }
    Thread.sleep(600000)
  }
}
