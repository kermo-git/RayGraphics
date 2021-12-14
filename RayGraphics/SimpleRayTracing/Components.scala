package RayGraphics.SimpleRayTracing

import RayGraphics.Geometry.{ORIGIN, Vec3}
import RayGraphics.Color
import RayGraphics.LinearColors.BLACK
import RayGraphics.Components.{Camera, Renderable, PointLightScene}

object Components {
  class SceneRenderer(val camera: Camera,
                      val scene: PointLightScene[Material],
                      val maxBounces: Int) extends Renderable {

    val imageWidth: Int = camera.imageWidth
    val imageHeight: Int = camera.imageHeight

    override def getPixelColor(x: Int, y: Int): Color =
      castRay(ORIGIN, camera.getCameraRay(x, y))

    def castRay(origin: Vec3, direction: Vec3, depth: Int = 0, inside: Boolean = false): Color = {
      if (depth > maxBounces) BLACK
      else scene.trace(origin, direction) match {
        case scene.Nohit(color) => color
        case scene.HitInfo(material, hitPoint, normal) =>
          material.shade(this, direction, hitPoint, normal, depth, inside)
      }
    }
  }

  trait Material {
    def shade(renderer: SceneRenderer,
              incident: Vec3,
              hitPoint: Vec3,
              normal: Vec3,
              recDepth: Int,
              inside: Boolean): Color
  }
}
