import Graphics3D._

import Geometry._
import Color._
import Components._
import Textures.SampleTextures._
import Textures.TextureUtils._
import Materials._
import Shapes._
import Graphics3D.Scenes._

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

  val rayTracingTest = new RayTracingScene(
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

  val rayMarchingTest = new RayMarchingScene(
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

  val MCfurnaceTest = new MonteCarloScene(
    imageWidth = 600,
    imageHeight = 600,

    samplesPerPixel = 100,
    background = _ => WHITE,

    shapes = List(
      Sphere(
        center = Vec3(0, 0, 20),
        radius = 5,
        material = MCDiffuse(MEDIUM_BLUE)
      )
    )
  )

  val cornellBox = new MonteCarloScene(
    imageWidth = 600,
    imageHeight = 600,

    samplesPerPixel = 1000,
    maxBounces = 5,

    shapes = List(
      // WALLS
      Plane(
        point = Vec3(-15, 0, 0),
        normal = UNIT_X,
        material = MCDiffuse(RED)
      ),
      Plane(
        point = Vec3(15, 0, 0),
        normal = UNIT_X,
        material = MCDiffuse(LIME)
      ),
      Plane(
        point = Vec3(0, 0, -1),
        normal = UNIT_Z,
        material = MCDiffuse(WHITE)
      ),
      Plane(
        point = Vec3(0, 0, 40),
        normal = UNIT_Z,
        material = MCDiffuse(WHITE)
      ),
      Plane(
        point = Vec3(0, 15, 0),
        normal = UNIT_Y,
        material = MCDiffuse(WHITE)
      ),
      Plane(
        point = Vec3(0, -15, 0),
        normal = UNIT_Y,
        material = MCDiffuse(WHITE)
      ),

      // LIGHT SOURCE
      Box(lenX = 10, lenY = 30, lenZ = 10,
        transformation = new Transformation(0, 29, 30),
        material = AreaLight(WHITE, intensity = 6)
      ),

      // OBJECTS

      Box(lenX = 10, lenY = 20, lenZ = 7,
        transformation = new Transformation(0, -30, 0, -6, -7, 34),
        material = MCDiffuse(WHITE)
      ),
      Box(lenX = 10, lenY = 10, lenZ = 7,
        transformation = new Transformation(0, 30, 0, 6, -10, 30),
        material = MCDiffuse(WHITE)
      )
    )
  )

  val cornellBoxBalls = new MonteCarloScene(
    imageWidth = 600,
    imageHeight = 600,

    samplesPerPixel = 1000,
    maxBounces = 5,

    shapes = List(
      // WALLS
      Plane(
        point = Vec3(-15, 0, 0),
        normal = UNIT_X,
        material = MCDiffuse(RED)
      ),
      Plane(
        point = Vec3(15, 0, 0),
        normal = UNIT_X,
        material = MCDiffuse(LIME)
      ),
      Plane(
        point = Vec3(0, 0, -1),
        normal = UNIT_Z,
        material = MCDiffuse(WHITE)
      ),
      Plane(
        point = Vec3(0, 0, 30),
        normal = UNIT_Z,
        material = MCDiffuse(WHITE)
      ),
      Plane(
        point = Vec3(0, 15, 0),
        normal = UNIT_Y,
        material = MCDiffuse(WHITE)
      ),
      Plane(
        point = Vec3(0, -15, 0),
        normal = UNIT_Y,
        material = MCDiffuse(WHITE)
      ),

      // LIGHT SOURCE
      Box(lenX = 10, lenY = 30, lenZ = 10,
        transformation = new Transformation(0, 29.5, 25),
        material = AreaLight(WHITE, intensity = 8)
      ),

      // OBJECTS

      Sphere(
        center = Vec3(-11, -11, 26),
        radius = 4,
        material = MCDiffuse(AQUAMARINE)
      ),
      Sphere(
        center = Vec3(0, -11, 26),
        radius = 4,
        material = MCDiffuse(GOLD)
      ),
      Sphere(
        center = Vec3(11, -11, 26),
        radius = 4,
        material = MCDiffuse(LIGHT_SKY_BLUE)
      )
    )
  )

  val glowingSphere = new MonteCarloScene(
    imageWidth = 600,
    imageHeight = 600,

    samplesPerPixel = 120,
    maxBounces = 5,

    background = _ => WHITE,

    shapes = List(
      Plane(
        point = Vec3(0, -5, 0),
        normal = UNIT_Y,
        material = MCDiffuse(SEAGREEN)
      ),
      Plane(
        point = Vec3(0, 0, 50),
        normal = Vec3(1, 0, -1).normalize,
        material = MCDiffuse(DEEP_SKY_BLUE)
      ),
      Plane(
        point = Vec3(0, 0, 50),
        normal = Vec3(-1, 0, -1).normalize,
        material = MCDiffuse(ORANGE)
      ),
      Sphere(
        center = Vec3(-8, 2, 30),
        radius = 7,
        material = AreaLight(LAWN_GREEN, intensity = 2)
      ),
      Sphere(
        center = Vec3(8, 2, 30),
        radius = 7,
        material = MCDiffuse(LIGHT_SKY_BLUE)
      ),
    )
  )
}
