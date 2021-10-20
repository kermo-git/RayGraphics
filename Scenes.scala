import Graphics3D._

import Geometry._
import LinearColors._
import Components._
import MonteCarlo._
import Textures.SampleTextures._
import Textures.TextureUtils._
import Materials._
import Shapes._
import Graphics3D.Scenes._

object Scenes {
  def colorTest(color: Color): Renderable = new Renderable {
    override val imageWidth: Int = 600
    override val imageHeight: Int = 600

    override def getPixelColor(x: Int, y: Int): Color = color
  }

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
      PointLight(location = Vec3(-14, 14, 0))
    ),

    shapes = List(
      Cone(height = 15, radius = 7,
        transformation = new Transformation(7, -15, 29),
        material = Phong(GOLD)
      ),
      Sphere(
        center = Vec3(-5, 0, 22),
        radius = 5,
        material = Phong(GOLD)
      ),
      Cylinder(height = 20, radius = 6,
        transformation = new Transformation(-88.2, -36, 0, 4, 7, 39),
        material = Phong(GOLD)
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
        material = Metal(SADDLE_BROWN)
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
        material = BDRFMaterial(new Dielectric(color = RED, alpha = 0.8))
      ),
      Plane(
        point = Vec3(15, 0, 0),
        normal = UNIT_X,
        material = BDRFMaterial(new Dielectric(color = LIME, alpha = 0.8))
      ),
      Plane(
        point = Vec3(0, 0, -1),
        normal = UNIT_Z,
        material = BDRFMaterial(new Dielectric(color = WHITE, alpha = 0.8))
      ),
      Plane(
        point = Vec3(0, 0, 40),
        normal = UNIT_Z,
        material = BDRFMaterial(new Dielectric(color = WHITE, alpha = 0.8))
      ),
      Plane(
        point = Vec3(0, 15, 0),
        normal = UNIT_Y,
        material = BDRFMaterial(new Dielectric(color = WHITE, alpha = 0.8))
      ),
      Plane(
        point = Vec3(0, -15, 0),
        normal = UNIT_Y,
        material = BDRFMaterial(new Dielectric(color = WHITE, alpha = 0.8))
      ),

      // LIGHT SOURCE
      Sphere(
        center = Vec3(0, 20, 25),
        radius = 9,
        material = BDRFMaterial(new Dielectric(color = GRAY, alpha = 0.8, emission = WHITE * 30))
      ),

      // OBJECTS
      Box(lenX = 10, lenY = 20, lenZ = 7,
        transformation = new Transformation(0, -30, 0, -6, -7, 34),
        material = BDRFMaterial(new Dielectric(color = WHITE, alpha = 0.8))
      ),
      Box(lenX = 10, lenY = 10, lenZ = 7,
        transformation = new Transformation(0, 30, 0, 6, -10, 30),
        material = BDRFMaterial(new Dielectric(color = WHITE, alpha = 0.8))
      )
    )
  ) with MonteCarloScene { val samplesPerPixel = 100 }

  val cornellBoxBalls: Scene = new RayTracingScene(
    camera = camera,
    maxBounces = 10,

    shapes = List(
      // WALLS
      Plane(
        point = Vec3(-15, 0, 0),
        normal = UNIT_X,
        material = BDRFMaterial(new Dielectric(color = RED, alpha = 0.8))
      ),
      Plane(
        point = Vec3(15, 0, 0),
        normal = UNIT_X,
        material = BDRFMaterial(new Dielectric(color = LIME, alpha = 0.8))
      ),
      Plane(
        point = Vec3(0, 0, -1),
        normal = UNIT_Z,
        material = BDRFMaterial(new Dielectric(color = WHITE, alpha = 0.8))
      ),
      Plane(
        point = Vec3(0, 0, 50),
        normal = UNIT_Z,
        material = BDRFMaterial(new Dielectric(color = WHITE, alpha = 0.8))
      ),
      Plane(
        point = Vec3(0, 15, 0),
        normal = UNIT_Y,
        material = BDRFMaterial(new Dielectric(color = WHITE, alpha = 0.8))
      ),
      Plane(
        point = Vec3(0, -15, 0),
        normal = UNIT_Y,
        material = BDRFMaterial(new Dielectric(color = WHITE, alpha = 0.8))
      ),

      // LIGHT SOURCE
      Sphere(
        center = Vec3(0, 20, 25),
        radius = 9,
        material = BDRFMaterial(new Dielectric(color = GRAY, alpha = 0.8, emission = WHITE * 30))
      ),

      // OBJECTS
      Sphere(
        center = Vec3(0, -5, 40),
        radius = 10,
        material = BDRFMaterial(new Metallic(color = GOLD, alpha = 0.2))
      ),
      Sphere(
        center = Vec3(10, -10, 30),
        radius = 5,
        material = BDRFMaterial(new Dielectric(color = DEEP_SKY_BLUE, alpha = 0.2))
      ),
    )
  ) with MonteCarloScene { val samplesPerPixel = 100 }
}
