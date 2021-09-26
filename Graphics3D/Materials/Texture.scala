package Graphics3D.Materials

import Graphics3D.Geometry._
import Graphics3D.Color
import Graphics3D.Components._

case class Texture(textureFunction: TextureFunction,
                   textureScale: Double,
                   shininess: Double = 128) extends Material {

  override def shade(scene: PointLightScene,
                     incident: Vec3,
                     hitPoint: Vec3,
                     normal: Vec3,
                     depth: Int,
                     inside: Boolean): Color = {

    val textureColor = textureFunction(hitPoint * textureScale)
    Phong(diffuse = textureColor, shininess = shininess).shade(scene, incident, hitPoint, normal, depth, inside)
  }
}
