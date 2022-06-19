import scala.math.Pi

import RayGraphics._
import Geometry._
import LinearColors._
import Components._
import Textures.TextureUtils.scaleTexture
import Shapes._
import PathTracing._
import PathTracing.Components._
import SampleTextures.CLOUDS

object TestBidirectionalTracing {
  def scene(): Renderable = {
    val lightCenter: Vec3 = Vec3(-5, 0, 25)
    val lightRadius: Double = 2
    val lightColor: Color = WHITE * 100
    val lightSurfaceArea = 4 * Pi * lightRadius * lightRadius

    def getLightSample(): LightSample = {
      val normal = randUnitVector
      val lightDirection = (normal + randUnitVector).normalize
      val lightDirectionPDF = (normal dot lightDirection) * divideByPi

      LightSample(
        location = lightCenter + normal * (lightRadius + SURFACE_BIAS),
        direction = lightDirection,
        emission = lightColor * (lightSurfaceArea / lightDirectionPDF)
      )
    }

    val lightSource = Sphere(
      center = lightCenter,
      radius = lightRadius,
      material = Lambert(
        color = MEDIUM_SLATE_BLUE,
        emission = lightColor
      )
    )

    val ball = Sphere(
      center = Vec3(5, -3, 20),
      radius = 4,
      material = Lambert(SEAGREEN)
    )

    val room = Box(
      lenX = 20, lenZ = 30, lenY = 15,
      transformation = new Transformation(0, 0, 14),
      material = Lambert(LIGHT_GRAY)
    )

    BidirectionalTracer(
      camera = Camera(600, 600, 65),
      samplesPerPixel = 5,
      getLightSample = getLightSample,
      scene = RayTracingScene(
        background = scaleTexture(CLOUDS, 4),
        shapes = List(room, lightSource, ball),
      )
    )
  }
}
