package RayGraphics.SimpleRayTracing

import RayGraphics.Geometry._
import RayGraphics.Color
import RayGraphics.LinearColors.WHITE
import Components.{Material, SceneRenderer}

case class DummyMaterial(color: Color = WHITE) extends Material {
  override def shade(renderer: SceneRenderer, incident: Vec3, hitPoint: Vec3, normal: Vec3, recDepth: Int, inside: Boolean): Color = {
    color * -(incident dot normal)
  }
}
