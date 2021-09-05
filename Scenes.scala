import Graphics3D._
import GeometryUtils._
import Components._
import Colors._
import Textures.SampleTextures._
import Textures.TextureUtils._
import Materials._
import Shapes._

object Scenes {
  val noise = new NoiseDisplay(
    imageWidth = 600,
    imageHeight = 600,
    unitSizePx = 30,
    noise = octaveNoise(perlinNoise)()
  )

  val texture = new TextureDisplay(
    imageWidth = 600,
    imageHeight = 600,
    unitSizePx = 100,
    textureZ = 0.3,
    texture = FIRE
  )

  val testScene = new RayMarchingScene(
    imageWidth = 600,
    imageHeight = 600,

    background = EXPLOSION,
    backGroundScale = 4,

    lights = List(Light(0, 0, 0)),

    shapes = List(
      Plane(
        point = Vec3(0, -8, 0),
        material = Metal(HONEYDEV)
      ),
      Torus(
        mainRadius = 10,
        tubeRadius = 3,
        transformation = new Transformation(-30, 0, -30, -7, 5, 30),
        material = Metal(LIGHT_SKY_BLUE)
      ),
      Sphere(
        center = Vec3(15, 5, 30),
        radius = 6,
        material = Metal(AQUAMARINE)
      )
    )
  )
}
