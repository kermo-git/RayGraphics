package RayGraphics.SimpleRayTracing

import RayGraphics.Geometry._
import RayGraphics.Color
import RayGraphics.LinearColors.WHITE
import Components.{Material, SimpleRayTracer}

case class DummyMaterial(color: Color = WHITE) extends Material {
  override def shade(renderer: SimpleRayTracer, incident: Vec3, hitPoint: Vec3, normal: Vec3, recDepth: Int, inside: Boolean): Color = {
    color * -(incident dot normal)
  }
}
