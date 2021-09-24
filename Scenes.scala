import Graphics3D._
import Geometry._
import Components._
import Colors._
import Graphics3D.Scenes.{RayMarchingScene, RayTracingScene}
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

  val testScene1 = new RayTracingScene(
    imageWidth = 600,
    imageHeight = 600,

    lights = List(
      PointLight(location = Vec3(-14, 14, 0), intensity = 40000)
    ),

    shapes = List(
      Cone(height = 15, radius = 7,
        transformation = new Transformation(7, -15, 29),
        material = Phong(FIREBRICK)
      ),
      Sphere(
        center = Vec3(-5, 0, 22),
        radius = 5,
        material = Glass()
      ),
      Cylinder(height = 20, radius = 6,
        transformation = new Transformation(-88.2, -36, 0, 4, 7, 39),
        material = Phong(SEAGREEN)
      ),
      Box(lenX = 30, lenY = 30, lenZ = 50,
        transformation = new Transformation(0, 0, 24),
        material = Phong(GOLD)
      )
    )
  )

  val testScene2 = new RayMarchingScene(
    imageWidth = 600,
    imageHeight = 600,

    background = EXPLOSION,
    backGroundScale = 4,

    lights = List(
      PointLight(location = Vec3(0, 20, 30), intensity = 20000)
    ),

    shapes = List(
      Blend(
        smoothness = 5,
        material = Metal(HONEYDEV),

        shape1 = Plane(
          point = Vec3(0, -8, 0)
        ),
        shape2 = Torus(
          mainRadius = 10,
          tubeRadius = 3,
          transformation = new Transformation(10, -8, 30)
        )
      ),
      Torus(
        mainRadius = 10,
        tubeRadius = 3,
        transformation = new Transformation(-30, 0, -30, -7, 5, 30),
        material = Metal(DARK_CYAN)
      ),
      Sphere(
        center = Vec3(15, 5, 30),
        radius = 6,
        material = Metal(DEEP_SKY_BLUE)
      )
    )
  )

  val shadowScene = new RayMarchingScene(
    imageWidth = 600,
    imageHeight = 600,

    background = CLOUDS,
    backGroundScale = 4,

    lights = List(PointLight(location = Vec3(-40, 20, 30))),
    shadowStepMultiPlier = 0.3,

    shapes = List(
      Plane(
        point = Vec3(0, -8, 0)
      ),
      Cut(
        material = Phong(GOLD),

        shape = Box(
          lenX = 10,
          lenY = 30,
          lenZ = 20,
          transformation = new Transformation(0, 0, 0, -5, -8, 30)
        ),
        cut = Sphere(
          center = Vec3(-5, -2, 30),
          radius = 9
        )
      )
    )
  )
}
