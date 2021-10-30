import Graphics3D._
import Geometry._
import LinearColors._
import Components._
import Textures.SampleTextures._
import Textures.TextureUtils._
import Shapes._
import Vanilla._, Vanilla.Components.Light
import MonteCarlo._
import Textures.Components.{NoiseDisplay, TextureDisplay}

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
      Light(location = Vec3(-14, 14, 0))
    ),

    shapes = List(
      Cone(height = 15, radius = 7,
        transformation = new Transformation(7, -15, 29),
        material = ReflectivePhong(diffuse = ORANGE_RED, specular = GOLD)
      ),
      Sphere(
        center = Vec3(-5, 0, 22),
        radius = 5,
        material = Glass()
      ),
      Cylinder(height = 20, radius = 6,
        transformation = new Transformation(-88.2, -36, 0, 4, 7, 39),
        material = ReflectivePhong(LIME)
      ),
      Box(lenX = 30, lenY = 30, lenZ = 50,
        transformation = new Transformation(0, 0, 24),
        material = ReflectivePhong(SILVER)
      )
    )
  )

  val rayMarchingTest = new RayMarchingScene(
    camera = camera,

    background = EXPLOSION,
    backGroundScale = 4,

    pointLights = List(
      Light(location = Vec3(0, 20, 30))
    ),

    shapes = List(
      Blend(
        smoothness = 5,
        material = ReflectivePhong(HONEYDEV),

        shape1 = Plane(
          point = Vec3(0, -8, 0),
          material = null
        ),
        shape2 = Torus(
          mainRadius = 10,
          tubeRadius = 3,
          transformation = new Transformation(10, -8, 30),
          material = null
        )
      ),
      Torus(
        mainRadius = 10,
        tubeRadius = 3,
        transformation = new Transformation(-30, 0, -30, -7, 5, 30),
        material = ReflectivePhong(SADDLE_BROWN)
      ),
      Sphere(
        center = Vec3(15, 5, 30),
        radius = 6,
        material = ReflectivePhong(DEEP_SKY_BLUE)
      )
    )
  )

  val cornellBox: Renderable = new PathTracingScene(
    camera = camera,
    samplesPerPixel = 10,

    shapes = List(
      // WALLS
      Plane(
        point = Vec3(-15, 0, 0),
        normal = UNIT_X,
        material = MonteCarlo.Dielectric(HOT_PINK)
      ),
      Plane(
        point = Vec3(15, 0, 0),
        normal = UNIT_X,
        material = MonteCarlo.Dielectric(GOLD)
      ),
      Plane(
        point = Vec3(0, 0, -1),
        normal = UNIT_Z,
        material = MonteCarlo.Dielectric(LIGHT_GRAY)
      ),
      Plane(
        point = Vec3(0, 0, 40),
        normal = UNIT_Z,
        material = MonteCarlo.Dielectric(LIGHT_GRAY)
      ),
      Plane(
        point = Vec3(0, 15, 0),
        normal = UNIT_Y,
        material = MonteCarlo.Dielectric(LIGHT_GRAY)
      ),
      Plane(
        point = Vec3(0, -15, 0),
        normal = UNIT_Y,
        material = MonteCarlo.Dielectric(LIGHT_GRAY)
      ),

      // LIGHT SOURCE
      Sphere(
        center = Vec3(0, 20, 25),
        radius = 9,
        material = MonteCarlo.Dielectric(color = LIGHT_GRAY, emission = WHITE * 10)
      ),

      // OBJECTS
      Box(lenX = 10, lenY = 20, lenZ = 7,
        transformation = new Transformation(0, -30, 0, -6, -7, 34),
        material = MonteCarlo.Dielectric(LIGHT_GRAY)
      ),
      Box(lenX = 10, lenY = 10, lenZ = 7,
        transformation = new Transformation(0, 30, 0, 6, -10, 30),
        material = MonteCarlo.Dielectric(LIGHT_GRAY)
      )
    )
  )

  val cornellBoxBalls: Renderable = new PathTracingScene(
    camera = camera,
    samplesPerPixel = 10,

    shapes = List(
      // WALLS
      Plane(
        point = Vec3(-15, 0, 0),
        normal = UNIT_X,
        material = MonteCarlo.Dielectric(RED)
      ),
      Plane(
        point = Vec3(15, 0, 0),
        normal = UNIT_X,
        material = MonteCarlo.Dielectric(LIME)
      ),
      Plane(
        point = Vec3(0, 0, -1),
        normal = UNIT_Z,
        material = MonteCarlo.Dielectric(WHITE)
      ),
      Plane(
        point = Vec3(0, 0, 50),
        normal = UNIT_Z,
        material = MonteCarlo.Dielectric(WHITE)
      ),
      Plane(
        point = Vec3(0, 15, 0),
        normal = UNIT_Y,
        material = MonteCarlo.Dielectric(WHITE)
      ),
      Plane(
        point = Vec3(0, -15, 0),
        normal = UNIT_Y,
        material = MonteCarlo.Dielectric(WHITE)
      ),

      // LIGHT SOURCE
      Sphere(
        center = Vec3(0, 20, 25),
        radius = 9,
        material = MonteCarlo.Dielectric(color = GRAY, emission = WHITE * 15)
      ),

      // OBJECTS
      Sphere(
        center = Vec3(0, -5, 40),
        radius = 10,
        material = MonteCarlo.Dielectric(color = GOLD, roughness = 0.3, reflectivity = 0.3)
      ),
      Sphere(
        center = Vec3(10, -10, 30),
        radius = 5,
        material = MonteCarlo.Dielectric(color = DEEP_SKY_BLUE, reflectivity = 0.3)
      ),
    )
  )
}
