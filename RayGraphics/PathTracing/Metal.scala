package RayGraphics.PathTracing

import RayGraphics.Geometry._
import RayGraphics.Color
import RayGraphics.LinearColors.BLACK
import Components.BRDFResult

case class Metal(color: Color,
                 emission: Color = BLACK,
                 override val roughness: Double = 0.2) extends CookTorrance(roughness) {

  override def sample(view: Vec3, normal: Vec3): Vec3 = {
    val half = randMicroNormal(normal)
    reflection(view.invert, half).normalize
  }

  override def PDF(view: Vec3, normal: Vec3, light: Vec3): Double = {
    val half = (view + light).normalize
    val halfDotNormal = half dot normal
    D_GGX(halfDotNormal) * halfDotNormal
  }

  override def BRDF(view: Vec3, normal: Vec3, light: Vec3): Color = {
    val half = (view + light).normalize

    val normalDotHalf = normal dot half
    val normalDotView = normal dot view
    val normalDotLight = normal dot light

    val f: Color = FresnelSchlick(view dot half, color)
    val d: Double = D_GGX(normalDotHalf)
    val g: Double = G1(normalDotView) * G1(normalDotLight)

    f * (d * g / (4 * normalDotView * normalDotLight))
  }

  override def evaluate(view: Vec3, normal: Vec3): BRDFResult = {
    val half = randMicroNormal(normal)
    val light = reflection(view.invert, half).normalize

    val normalDotHalf = normal dot half
    val normalDotView = normal dot view
    val normalDotLight = normal dot light

    val f: Color = FresnelSchlick(view dot half, color)
    val g: Double = G1(normalDotView) * G1(normalDotLight)

    BRDFResult(
      sample = light,
      albedo = f * (g / (4 * normalDotView * normalDotHalf))
    )
  }
}
