import java.awt.image.BufferedImage
import java.awt.Graphics
import javax.swing.JFrame
import javax.swing.JPanel
import Graphics3D.BaseObjects.Renderable

class Display(val image: BufferedImage) extends JPanel {
  override def paint(g: Graphics): Unit = {
    g.drawImage(image, 0, 0, this)
  }
}

object Main {
  def sceneToImage(scene: Renderable): BufferedImage = {
    val image: BufferedImage = new BufferedImage(
      scene.imageWidth,
      scene.imageHeight,
      BufferedImage.TYPE_INT_RGB
    )
    for (x <- 0 until scene.imageWidth) {
      for (y <- 0 until scene.imageHeight) {
        val color = scene.getPixelColor(x, y)
        image.setRGB(x, y, color.toHex)
      }
    }
    image
  }

  def main(args: Array[String]): Unit = {
    val startTime = System.nanoTime

    val image: BufferedImage = sceneToImage(Scenes.noisyShape)
    val frame = new JFrame
    frame.setTitle("3D Graphics")
    frame.setSize(image.getWidth, image.getHeight)
    frame.add(new Display(image))
    frame.setVisible(true)

    val duration: Double = System.nanoTime - startTime
    println("Rendering took " + duration / 1e9 + " seconds")
  }
}
