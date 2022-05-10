package RayGraphics.PathTracing

import scala.math.random

import RayGraphics.Geometry.{Vec3, reflection}
import RayGraphics.Color
import RayGraphics.LinearColors.BLACK
import Components._

case class Dielectric(color: Color,
                      reflectivity: Double = 0.04,
                      override val emission: Color = BLACK,
                      override val roughness: Double = 0.5) extends CookTorrance with Material {

  val F0: Color = new Color(reflectivity, reflectivity, reflectivity)

  override def sample(view: Vec3, normal: Vec3): Vec3 = {
    val half = randMicroNormal(normal)
    val f = FresnelSchlick(view dot half, reflectivity)

    if (random() < f) {
      reflection(view.invert, half).normalize
    } else {
      (normal + randUnitVector).normalize
    }
  }

  // TODO: this is only for specular
  override def PDF(view: Vec3, normal: Vec3, light: Vec3): Double = {
    val half = (view + light).normalize
    val normalDotHalf = normal dot half
    D_GGX(normalDotHalf) * normalDotHalf
  }

  override def BRDF(view: Vec3, normal: Vec3, light: Vec3): Color = {
    val half = (view + light).normalize

    val normalDotHalf = normal dot half
    val normalDotView = normal dot view
    val normalDotLight = normal dot light

    val f: Color = FresnelSchlick(view dot half, F0)
    val d: Double = D_GGX(normalDotHalf)
    val g: Double = G1(normalDotView) * G1(normalDotLight)

    color * f.invert * divideByPi +
      f * (d * g / (4 * normalDotView * normalDotLight))
  }

  override def evaluate(view: Vec3, normal: Vec3): BRDFResult = {
    val half = randMicroNormal(normal)
    val f = FresnelSchlick(view dot half, reflectivity)

    if (random() < f) {
      val light = reflection(view.invert, half).normalize

      val normalDotHalf = normal dot half
      val normalDotView = normal dot view
      val normalDotLight = normal dot light

      val g = G1(normalDotView) * G1(normalDotLight)
      val r = g / (4 * normalDotView * normalDotHalf)

      BRDFResult(
        sample = light,
        albedo = new Color(r, r, r)
      )
    } else {
      val light = (normal + randUnitVector).normalize
      BRDFResult(
        sample = light,
        albedo = color
      )
    }
  }
}
