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
  def createScene(): Renderable = {
    val lightCenter: Vec3 = Vec3(-5, -1, 20)
    val lightRadius: Double = 4
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
        color = WHITE,
        emission = lightColor
      )
    )

    val room = Box(
      lenX = 20, lenZ = 30, lenY = 15,
      transformation = new Transformation(0, 0, 14),
      material = Lambert(SEAGREEN)
    )

    BidirectionalTracer(
      camera = Camera(600, 600, 65),
      samplesPerPixel = 100,
      getLightSample = getLightSample,
      scene = RayTracingScene(
        background = scaleTexture(CLOUDS, 4),
        shapes = List(room, lightSource),
      )
    )
  }

  val scene: Renderable = createScene()
}
