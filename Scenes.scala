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

  val camera: Camera = Camera(600, 600, 70)

  val rayTracingTest = new RayTracingScene(
    camera = camera,

    pointLights = List(
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
    camera = camera,

    background = EXPLOSION,
    backGroundScale = 4,

    pointLights = List(
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

  val cornellBox: Scene = new RayTracingScene(
    camera = camera,

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
  ) with MonteCarloScene { val samplesPerPixel = 100 }

  val cornellBoxBalls: Scene = new RayTracingScene(
    camera = camera,
    maxBounces = 5,

    shapes = List(
      // WALLS
      Plane(
        point = Vec3(-15, 0, 0),
        normal = UNIT_X,
        material = MCTexture(LAVA_ROCK, 0.2) // MCDiffuse(LAWN_GREEN)
      ),
      Plane(
        point = Vec3(15, 0, 0),
        normal = UNIT_X,
        material = MCDiffuse(LAWN_GREEN)
      ),
      Plane(
        point = Vec3(0, 0, -1),
        normal = UNIT_Z,
        material = MCDiffuse(WHITE)
      ),
      Plane(
        point = Vec3(0, 0, 35),
        normal = UNIT_Z,
        material = MCTexture(CLOUDS, 0.1) // MCDiffuse(WHITE)
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
      Sphere(
        center = Vec3(0, 21, 25),
        radius = 10,
        material = AreaLight(WHITE, intensity = 3)
      ),

      // OBJECTS

      Sphere(
        center = Vec3(-6, -3, 30),
        radius = 6,
        material = Glossy(
          diffuse = WHITE,
          specular = WHITE,
          reflectivity = 1,
          roughness = 0.3
        )
      ),
      Sphere(
        center = Vec3(6, -3, 25), // Vec3(9, -3, 30),
        radius = 6,
        material = MCTexture(LIGHT_BLUE_SPOTS, 0.4) /* Glossy(
          diffuse = DEEP_SKY_BLUE,
          specular = DEEP_SKY_BLUE,
          reflectivity = 0.5,
          roughness = 0.4
        ) */
      )
    )
  ) with MonteCarloScene { val samplesPerPixel = 1000 }
}
