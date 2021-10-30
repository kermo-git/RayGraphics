package Graphics3D.Vanilla

import Graphics3D.Geometry._
import Graphics3D.Color
import Graphics3D.LinearColors.WHITE
import Components.{Material, Scene}

case class DummyMaterial(color: Color = WHITE) extends Material {
  override def shade(scene: Scene, incident: Vec3, hitPoint: Vec3, normal: Vec3, recDepth: Int, inside: Boolean): Color = {
    color * -(incident dot normal)
  }
}
