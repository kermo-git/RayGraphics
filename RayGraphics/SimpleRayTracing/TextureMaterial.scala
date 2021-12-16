package RayGraphics.SimpleRayTracing

import RayGraphics.Geometry._
import RayGraphics.Color
import RayGraphics.Textures.Components.Texture
import Components.{Material, SimpleRayTracer}

case class TextureMaterial(texture: Texture,
                           shininess: Double = 128) extends Material {

  override def shade(renderer: SimpleRayTracer,
                     incident: Vec3,
                     hitPoint: Vec3,
                     normal: Vec3,
                     depth: Int,
                     inside: Boolean): Color = {

    val textureColor = texture(hitPoint)
    val material = Phong(textureColor, shininess = shininess)
    material.shade(renderer, incident, hitPoint, normal, depth, inside)
  }
}
