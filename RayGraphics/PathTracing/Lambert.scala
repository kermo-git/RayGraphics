package RayGraphics.PathTracing

import RayGraphics.Geometry.Vec3
import RayGraphics.Color
import RayGraphics.LinearColors.BLACK
import Components._

case class Lambert(color: Color, emission: Color = BLACK) extends Material {

  override def sample(view: Vec3, normal: Vec3): Vec3 = (normal + randUnitVector).normalize
  override def PDF(view: Vec3, normal: Vec3, light: Vec3): Double = (normal dot light) * divideByPi
  override def BRDF(view: Vec3, normal: Vec3, light: Vec3): Color = color * divideByPi

  override def evaluate(view: Vec3, normal: Vec3): BRDFResult =
    BRDFResult(
      sample = (normal + randUnitVector).normalize,
      albedo = color
    )
}
