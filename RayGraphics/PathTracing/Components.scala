package RayGraphics.PathTracing

import scala.util.Random
import scala.math.{Pi, random}

import RayGraphics.Geometry.Vec3
import RayGraphics.Color

object Components {
  case class BRDFResult(sample: Vec3, albedo: Color)

  trait Material {
    val emission: Color

    def sample(view: Vec3, normal: Vec3): Vec3
    def PDF(view: Vec3, normal: Vec3, light: Vec3): Double
    def BRDF(view: Vec3, normal: Vec3, light: Vec3): Color

    def evaluate(view: Vec3, normal: Vec3): BRDFResult = {
      val light = sample(view, normal)
      val cosineLaw = normal dot light
      val probability = PDF(view, normal, light)

      BRDFResult(
        sample = light,
        albedo = BRDF(view, normal, light) * (cosineLaw / probability)
      )
    }
  }

  val divideByPi: Double = 1 / Pi

  def randUnitVector: Vec3 = UNIT_VECTORS(Random.nextInt(UNIT_VECTORS.length))

  private lazy val UNIT_VECTORS: Seq[Vec3] = {
    def randSigned: Double = 2 * random() - 1
    def randVector: Vec3 = Vec3(randSigned, randSigned, randSigned)

    (for (_ <- 0 to 10000) yield randVector)
      .filter(vec => vec.length <= 1)
      .map(vec => vec.normalize)
  }
}
