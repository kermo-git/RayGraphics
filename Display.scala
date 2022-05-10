import java.awt.image.BufferedImage
import java.awt.Graphics
import javax.swing.JFrame
import javax.swing.JPanel

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

import RayGraphics.Components._
import RayGraphics.ColorUtils._

import java.io.File

object Main {
  def renderImage(renderable: Renderable): Future[BufferedImage] = {
    val w = renderable.imageWidth
    val h = renderable.imageHeight

    val image: BufferedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB)
    case class Pixel(x: Int, y: Int, color: Int)

    def paintPixel(x: Int, y: Int): Future[Pixel] = Future {
      val color = renderable.getPixelColor(x, y) apply reinhardToneMap apply tosRGB
      Pixel(x, y, color.toHex)
    }
    val pixels = for (x <- 0 until w; y <- 0 until h) yield paintPixel(x, y)

    Future.sequence(pixels).flatMap((pixelList: Seq[Pixel]) => {
      pixelList.foreach {
        case Pixel(x, y, color) => image.setRGB(x, y, color)
      }
      Future(image)
    })
  }

  def imageToFile(image: BufferedImage, filePath: String): Unit = {
    import javax.imageio.ImageIO
    val outputfile = new File(filePath)
    ImageIO.write(image, "jpg", outputfile)
  }

  def displayImageInWindow(image: BufferedImage): Unit = {

    class Display(val image: BufferedImage) extends JPanel {
      override def paint(g: Graphics): Unit = {
        g.drawImage(image, 0, 0, this)
      }
    }

    val frame = new JFrame
    frame.setTitle("3D Graphics")
    frame.setSize(image.getWidth, image.getHeight)
    frame.add(new Display(image))
    frame.setVisible(true)
  }

  def main(args: Array[String]): Unit = {
    val startTime = System.nanoTime

    renderImage(SampleScenes.pathTracingWithRayTracing).onComplete {
      case Success(image) =>
        val duration: Double = System.nanoTime - startTime
        println("Rendering took " + duration / 1e9 + " seconds")
        displayImageInWindow(image)
      case Failure(e) => e.printStackTrace()
    }
    Thread.sleep(2000000)
  }
}
