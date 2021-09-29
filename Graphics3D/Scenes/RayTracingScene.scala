package Graphics3D.Scenes

import scala.annotation.tailrec

import Graphics3D.Geometry.Vec3
import Graphics3D.Color, Color.BLACK
import Graphics3D.Components._

class RayTracingScene(imageWidth: Int,
                      imageHeight: Int,
                      FOVDegrees: Int = 70,

                      maxBounces: Int = 5,
                      renderShadows: Boolean = true,

                      val background: TextureFunction = _ => BLACK,
                      val backGroundScale: Double = 1,

                      lights: List[PointLight],
                      val shapes: List[RTShape[Material]]
                     )
  extends PointLightScene(
    imageWidth, imageHeight, FOVDegrees, maxBounces, renderShadows, lights
  ) {

  type Shape = RTShape[Material]

  override def castRay(origin: Vec3, direction: Vec3, depth: Int, inside: Boolean): Color = {
    trace(shapes, origin, direction) match {
      case None => background(direction * backGroundScale)
      case Some((shape, distance)) =>
        val hitPoint = origin + direction * distance
        val normal = shape.getNormal(hitPoint)
        val trueNormal = if ((direction dot normal) > 0) normal.invert else normal
        shape.material.shade(this, direction, hitPoint, trueNormal, depth, inside)
    }
  }

  override def getShadow(point: Vec3, light: PointLight): Double = {
    val pointToLight = new Vec3(light.location, point)
    val distToLight = pointToLight.length
    val shadowRayDirection = pointToLight.normalize

    @tailrec
    def shadowTest(_shapes: List[Shape]): Double = _shapes match {
      case Nil => 1
      case shape :: tail => shape.getRayHitDist(light.location, shadowRayDirection) match {
        case None => shadowTest(tail)
        case Some(distance) => if (distance < distToLight) 0 else shadowTest(tail)
      }
    }
    shadowTest(shapes)
  }
}
