package RayGraphics.PathTracing

import scala.math.{Pi, atan, cos, random, sin, sqrt}
import scala.util.Random

import RayGraphics.Geometry.{Vec3, coordinateSystem}
import RayGraphics.Color
import Components.Material

// Papers:
// https://lsi2.ugr.es/rosana/investigacion/tesis/phd_rmontes.pdf (Read this first)
// https://www.cs.cornell.edu/~srm/publications/EGSR07-btdf.pdf
// https://cseweb.ucsd.edu/~ravir/papers/brdf/brdf.pdf

abstract class CookTorrance(val roughness: Double) extends Material {
  // http://graphicrants.blogspot.com/2013/08/specular-brdf-reference.html
  // http://www.codinglabs.net/article_physically_based_rendering_cook_torrance.aspx

  val alpha: Double = roughness * roughness
  val alpha2: Double = alpha * alpha

  def D_GGX(normalDotHalf: Double): Double = {
    val x = normalDotHalf * normalDotHalf * (alpha2 - 1) + 1
    alpha2 / (Pi * x * x)
  }

  val k: Double = alpha * sqrt(2 / Pi)

  def G1(normalDotVec: Double): Double = {
    normalDotVec / (normalDotVec * (1 - k) + k)
  }

  def FresnelSchlick(cos: Double, F0: Color): Color = {
    val _cos = 1 - cos
    F0 + F0.invert * _cos * _cos * _cos * _cos * _cos
  }

  def FresnelSchlick(cos: Double, F0: Double): Double = {
    val _cos = 1 - cos
    F0 + (1 - F0) * _cos * _cos * _cos * _cos * _cos
  }

  private val MICROFACET_NORMALS: Seq[Vec3] = for (_ <- 0 to 1000) yield {
    val r1 = random()
    val r2 = random()

    val theta = atan(alpha2 * sqrt(r1 / (1 - r1)))
    val phi = 2 * Pi * r2

    Vec3(
      sin(theta) * cos(phi),
      cos(theta),
      sin(theta) * sin(phi),
    ).normalize
  }

  def randMicroNormal(macroNormal: Vec3): Vec3 = {
    val microNormal = MICROFACET_NORMALS(Random.nextInt(1000))
    microNormal * coordinateSystem(macroNormal)
  }
}
