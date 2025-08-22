package RayGraphics.SimpleRayTracing

import RayGraphics.Geometry.Vec3
import RayGraphics.Color
import RayGraphics.LinearColors.{BLACK, WHITE}
import RayGraphics.Components._

object Components {
  case class PointLight(location: Vec3, color: Color = WHITE)
  
  case class SimpleRayTracer(camera: Camera,
                             scene: Scene[Material],
                             lights: List[PointLight],
                             maxBounces: Int) extends Renderable {

    val imageWidth: Int = camera.imageWidth
    val imageHeight: Int = camera.imageHeight

    override def getPixelColor(x: Int, y: Int): Color =
      castRay(camera.location, camera.getCameraRay(x, y))

    def castRay(origin: Vec3, direction: Vec3, depth: Int = 0, inside: Boolean = false): Color = {
      if (depth > maxBounces) BLACK
      else scene.trace(origin, direction) match {
        case Nohit(color) => color
        case HitInfo(material, hitPoint, normal) =>
          material.shade(this, direction, hitPoint, normal, depth, inside)
      }
    }
  }

  trait Material {
    def shade(renderer: SimpleRayTracer,
              incident: Vec3,
              hitPoint: Vec3,
              normal: Vec3,
              recDepth: Int,
              inside: Boolean): Color
  }
}
