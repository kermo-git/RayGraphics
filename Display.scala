import Graphics3D.BaseObjects.{Scene, Shape}

import java.awt.image.BufferedImage
import java.awt.Graphics
import javax.swing.JFrame
import javax.swing.JPanel
import Graphics3D.Color

class Display(val image: BufferedImage) extends JPanel {
  override def paint(g: Graphics): Unit = {
    g.drawImage(image, 0, 0, this)
  }
}

object Main {
  def sceneToImage[O <: Shape](scene: Scene[O]): BufferedImage = {
    var image: BufferedImage = null

    def initImage(numPixelsX: Int, numPixelsY: Int): Unit = {
      image = new BufferedImage(
        numPixelsX,
        numPixelsY,
        BufferedImage.TYPE_INT_RGB
      )
    }
    def setPixelColor(x: Int, y: Int, color: Color): Unit = {
      image.setRGB(x, y, color.toHex)
    }
    scene.render(initImage, setPixelColor)
    image
  }

  def main(args: Array[String]): Unit = {
    val startTime = System.nanoTime

    val image: BufferedImage = sceneToImage(Scenes.test)
    val frame = new JFrame
    frame.setTitle("3D Graphics")
    frame.setSize(image.getWidth, image.getHeight)
    frame.add(new Display(image))
    frame.setVisible(true)

    val duration: Double = System.nanoTime - startTime
    println("Rendering took " + duration / 1e9 + " seconds")
  }
}
