import Graphics3D._
import Graphics3D.Utils._
import Graphics3D.BaseObjects._
import Graphics3D.Colors._
import Graphics3D.Materials._
import Graphics3D.Noise._
import Graphics3D.Shapes._

object Scenes {
  val perlinNoise = new NoiseDisplay(
    imageWidth = 600,
    imageHeight = 600,
    unitSizePx = 30,
    noise = Noise.perlinNoise
  )

  val testScene = new RayMarchingScene(
    imageWidth = 600,
    imageHeight = 600,

    lights = List(Light(-14, 14, 0)),

    shapes = List(
      Cone(height = 15, radius = 7,
        trans = new Transformation(7, -15, 29),
        material = Matte(FIREBRICK)
      ),
      Sphere(
        center = Vec3(-5, 0, 22),
        radius = 5,
        material = Matte(ORANGE)
      ),
      Cylinder(height = 20, radius = 6,
        trans = new Transformation(-88.2, -36, 0, 4, 7, 39),
        material = Matte(ORANGE_RED)
      ),
      Box(lenX = 30, lenY = 30, lenZ = 50,
        trans = new Transformation(0, 0, 24),
        material = Matte(AZURE)
      )
    )
  )

  val torusAndShadow = new RayMarchingScene(
    imageWidth = 600,
    imageHeight = 600,

    lights = List(Light(15, 20, 25, shadowSharpness = 8)),
    softShadows = true,

    shapes = List(
      Plane(
        point = Vec3(0, -8, 0),
        normal = unitY,
        material = Matte(MEDIUM_VIOLET_RED)
      ),
      Torus(mainRadius = 6, tubeRadius = 2,
        trans = new Transformation(-50, 0, 0, 7, 0, 25),
        material = Matte(ORANGE_RED)
      )
    )
  )

  val cubeHighLights = new RayMarchingScene(
    imageWidth = 600,
    imageHeight = 600,

    lights = List(Light(-10, 15, 30), Light(20, 0, 30), Light(-15, -20, 25)),

    shapes = List(
      Box(lenX = 10, lenY = 10, lenZ = 10,
        trans = new Transformation(-40, 30, 0, 0, 0, 20),
        material = Matte(ORANGE_RED)
      )
    )
  )

  val insideCylinder = new RayMarchingScene(
    imageWidth = 600,
    imageHeight = 600,

    lights = List(Light(-4, 0, 15)),

    shapes = List(
      Cylinder(height = 20, radius = 5,
        trans = new Transformation(89, 0, 0, 0, 0, 10),
        material = Matte(IVORY)
      )
    )
  )

  val noisyShape = new RayMarchingScene(
    imageWidth = 600,
    imageHeight = 600,
    FOVDegrees = 30,

    lights = List(Light(0, 0, 0)),

    shapes = List(
      NoisyShape(
        shape = Torus(mainRadius = 5, tubeRadius = 3,
          trans = new Transformation(-50, 0, 0, 0, 0, 15),
          material = Matte(DEEP_SKY_BLUE)
        ),
        noise = Noise.fractalNoise(Noise.perlinNoise)(),
        noiseFrequency = 2,
        noiseAmplifier = 0.5,
        stepScale = 0.5
      )
    )
  )
}
