package Graphics3D.Vanilla

import Graphics3D.Geometry.{ORIGIN, Vec3}
import Graphics3D.Color
import Graphics3D.LinearColors.{BLACK, WHITE}
import Graphics3D.Components.{Camera, Renderable}
import Graphics3D.RayObjectFunctions.HitInfo
import Graphics3D.Textures.Components.TextureFunction

object Components {
  abstract class Scene(val camera: Camera,
                       val maxBounces: Int,
                       val pointLights: List[Light],
                       val background: TextureFunction = _ => BLACK,
                       val backgroundScale: Double = 1) extends Renderable {

    val imageWidth: Int = camera.imageWidth
    val imageHeight: Int = camera.imageHeight

    override def getPixelColor(x: Int, y: Int): Color =
      castRay(ORIGIN, camera.getCameraRay(x, y))

    def castRay(origin: Vec3, direction: Vec3, depth: Int = 0, inside: Boolean = false): Color = {
      if (depth > maxBounces) BLACK
      else traceRay(origin, direction) match {
        case None =>
          background(direction * backgroundScale)
        case Some(HitInfo(material, hitPoint, normal)) =>
          material.shade(this, direction, hitPoint, normal, depth, inside)
      }
    }
    def visibility(point1: Vec3, point2: Vec3): Boolean
    def traceRay(origin: Vec3, direction: Vec3): Option[HitInfo[Material]]
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
