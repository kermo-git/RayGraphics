import Graphics3D._
import GeometryUtils._
import Components._
import Colors._
import Textures.TextureFunctions._
import Textures.NoiseFunctions._
import Materials._
import Shapes._

object Scenes {
  val darkWood: TextureFunction = wood(absPerlinNoise)(10, 0.2)(BLACK, SADDLE_BROWN)
  val lightWood: TextureFunction = wood(absPerlinNoise)(10, 0.2)(TAN, WHEAT)
  val beautiful: TextureFunction = smoothColorBands(perlinNoise)(DARK_BLUE, MEDIUM_BLUE, DEEP_SKY_BLUE, MEDIUM_BLUE)

  val noise = new NoiseDisplay(
    imageWidth = 600,
    imageHeight = 600,
    unitSizePx = 30,
    noise = perlinNoise
  )

  val texture = new TextureDisplay(
    imageWidth = 600,
    imageHeight = 600,
    unitSizePx = 100,
    noiseZ = 0.3,
    texture = darkWood
  )

  val testScene = new RayTracingScene(
    imageWidth = 600,
    imageHeight = 600,

    lights = List(Light(-14, 14, 0)),

    shapes = List(
      Cone(height = 15, radius = 7,
        transformation = new Transformation(7, -15, 29),
        material = Matte(FIREBRICK)
      ),
      Sphere(
        center = Vec3(-5, 0, 22),
        radius = 5,
        material = Matte(ORANGE)
      ),
      Cylinder(height = 20, radius = 6,
        transformation = new Transformation(-88.2, -36, 0, 4, 7, 39),
        material = Matte(ORANGE_RED)
      ),
      Box(lenX = 30, lenY = 30, lenZ = 50,
        transformation = new Transformation(0, 0, 24),
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
        transformation = new Transformation(-50, 0, 0, 7, 0, 25),
        material = Matte(ORANGE_RED)
      )
    )
  )

  val cubeHighLights = new RayMarchingScene(
    imageWidth = 600,
    imageHeight = 600,

    lights = List(
      Light(-10, 15, 30),
      Light(20, 0, 30),
      Light(-15, -20, 25)
    ),

    shapes = List(
      Box(lenX = 10, lenY = 10, lenZ = 10,
        transformation = new Transformation(-40, 30, 0, 0, 0, 20),
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
        transformation = new Transformation(89, 0, 0, 0, 0, 10),
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
        material = Metal(DEEP_SKY_BLUE),
        shape = Torus(mainRadius = 5, tubeRadius = 3,
          transformation = new Transformation(-50, 0, 0, 0, 0, 15)
        ),
        noise = perlinNoise,
        noiseFrequency = 2,
        noiseAmplifier = 0.3
      )
    )
  )

  val blendedShapes = new RayMarchingScene(
    imageWidth = 600,
    imageHeight = 600,
    softShadows = true,

    lights = List(Light(10, 0, 0, shadowSharpness = 10)),

    shapes = List(
      Plane(point = Vec3(0, -8, 0), normal = unitY, material = Matte(TURQUOISE)),
      Blend(
        smoothness = 5,
        material = Texture(
          textureFunction = beautiful,
          textureScale = 0.5
        ),

        shape1 = Blend(
          smoothness = 5,
          shape1 = Torus(
            mainRadius = 10,
            tubeRadius = 5,
            transformation = new Transformation(-40, 0, 0, -2, 5, 40)
          ),
          shape2 = Sphere(
            center = Vec3(-19, 5, 40),
            radius = 7
          )
        ),
        shape2 = Cylinder(
          height = 20, radius = 7,
          transformation = new Transformation(-50, 0, 0, 13, 5, 40)
        )
      )
    )
  )

  val intersectionShape = new RayMarchingScene(
    imageWidth = 600,
    imageHeight = 600,

    lights = List(
      Light(0, 5, 0, shadowSharpness = 5, color = IVORY),
      Light(0, 0, 20, shadowSharpness = 5, color = GRAY)
    ),

    shapes = List(
      Plane(
        point = Vec3(0, -8, 0),
        normal = unitY,
        material = Texture(beautiful, textureScale = 0.5)
      ),
      Cut(
        material = Texture(lightWood, textureScale = 0.3),
        shape = Intersection(
          shape1 = Box(
            lenX = 10, lenY = 10, lenZ = 3,
            transformation = new Transformation(0, 50, 0, 0, 0, 10)
          ),
          shape2 = Sphere(
            center = Vec3(0, 0, 10),
            radius = 3
          )
        ),
        cut = Torus(
          mainRadius = 3,
          tubeRadius = 0.6,
          transformation = new Transformation(-1, 0, 6)
        )
      ),
    )
  )
}
