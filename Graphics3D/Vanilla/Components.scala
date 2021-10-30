package Graphics3D.Vanilla

import Graphics3D.Geometry.{ORIGIN, Vec3}
import Graphics3D.Color
import Graphics3D.LinearColors.WHITE
import Graphics3D.Components.{Camera, Renderable}

object Components {
  abstract class Scene(val camera: Camera,
                       val maxBounces: Int,
                       val pointLights: List[Light]) extends Renderable {

    val imageWidth: Int = camera.imageWidth
    val imageHeight: Int = camera.imageHeight

    override def getPixelColor(x: Int, y: Int): Color =
      castRay(ORIGIN, camera.getCameraRay(x, y))

    def castRay(origin: Vec3, direction: Vec3, depth: Int = 0, inside: Boolean = false): Color
    def visibility(point1: Vec3, point2: Vec3): Boolean
  }

  case class Light(location: Vec3, color: Color = WHITE)

  trait Material {
    def shade(scene: Scene,
              incident: Vec3,
              hitPoint: Vec3,
              normal: Vec3,
              recDepth: Int,
              inside: Boolean): Color
  }
}
