package Graphics3D.Materials

import Graphics3D.Geometry._
import Graphics3D.Components._
import Graphics3D.Color
import Graphics3D.LinearColors.WHITE

case class DummyMaterial(color: Color = WHITE) extends Material {
  override def shade(scene: Scene, incident: Vec3, hitPoint: Vec3, normal: Vec3, recDepth: Int, inside: Boolean): Color = {
    color * -(incident dot normal)
  }
}
