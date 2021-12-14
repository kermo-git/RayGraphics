import RayGraphics._
import Geometry._
import LinearColors._
import Components._
import Textures.SampleTextures._
import Textures.TextureUtils._
import Shapes._
import SimpleRayTracing._
import SimpleRayTracing.Components._
import PathTracing._
import RayGraphics.Textures.Components.scaleTexture

object Scenes {
  val camera: Camera = Camera(600, 600, 70)

  val rayTracingTest = new SceneRenderer(
    camera = camera,
    maxBounces = 5,

    scene = new RTPointLightScene(
      lights = List(
        PointLight(location = Vec3(-14, 14, 0))
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
  )

  val rayMarchingTest = new SceneRenderer(
    camera = camera,
    maxBounces = 5,

    scene = new RMPointLightScene(
      maxDist = 100,
      background = scaleTexture(CLOUDS, 4),

      lights = List(
        PointLight(location = Vec3(0, 20, 30))
      ),
      shapes = List(
        NoisyShape(
          noise = perlinNoise,
          noiseFrequency = 0.5,
          noiseAmplifier = 1,

          shape = Blend(
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
          material = ReflectivePhong(HONEYDEV),
        ),

        Torus(
          mainRadius = 10,
          tubeRadius = 3,
          transformation = new Transformation(-30, 0, -30, -7, 5, 30),
          material = ReflectivePhong(AQUAMARINE)
        ),
        Sphere(
          center = Vec3(15, 5, 30),
          radius = 6,
          material = ReflectivePhong(DARK_VIOLET)
        )
      )
    ),
  )

  val spiral = new SceneRenderer(
    camera = camera,
    maxBounces = 5,

    scene = new RMPointLightScene(
      maxDist = 100,

      background = scaleTexture(CLOUDS, 7),

      lights = List(
        PointLight(location = Vec3(40, 20, 40))
      ),

      shapes = List(
        NoisyShape(
          shape = TransformedTorus(
            mainRadius = 5,
            tubeRadius = 1,

            twist = 0.1,
            elongationX = 5,
            elongationZ = 2,
            elongationY = 80,

            transformation = new Transformation(10, 0, 50, 0, 0, 60),
            stepScale = 0.7,

            material = ReflectivePhong(INDIGO)
          ),
          noise = octaveNoise(absPerlinNoise)(),
          noiseFrequency = 0.3,

          stepScale = 0.7,
          material = ReflectivePhong(OLIVE_DRAB)
        )
      )
    )
  )

  val cornellBox: Renderable = new PathTracer(
    camera = camera,
    samplesPerPixel = 10,

    scene = RayTracingScene(
      shapes = List(
        // WALLS
        Plane(
          point = Vec3(-15, 0, 0),
          normal = UNIT_X,
          material = Dielectric(RED)
        ),
        Plane(
          point = Vec3(15, 0, 0),
          normal = UNIT_X,
          material = Dielectric(LIME)
        ),
        Plane(
          point = Vec3(0, 0, -1),
          normal = UNIT_Z,
          material = Dielectric(WHITE)
        ),
        Plane(
          point = Vec3(0, 0, 40),
          normal = UNIT_Z,
          material = Dielectric(WHITE)
        ),
        Plane(
          point = Vec3(0, 15, 0),
          normal = UNIT_Y,
          material = Dielectric(WHITE)
        ),
        Plane(
          point = Vec3(0, -15, 0),
          normal = UNIT_Y,
          material = Dielectric(WHITE)
        ),

        // LIGHT SOURCE
        Sphere(
          center = Vec3(0, 20, 25),
          radius = 9,
          material = Dielectric(color = LIGHT_GRAY, emission = WHEAT * 10)
        ),

        // OBJECTS
        Box(lenX = 10, lenY = 20, lenZ = 7,
          transformation = new Transformation(0, -30, 0, -6, -7, 34),
          material = Dielectric(LIGHT_GRAY)
        ),
        Box(lenX = 10, lenY = 10, lenZ = 7,
          transformation = new Transformation(0, 30, 0, 6, -10, 30),
          material = Dielectric(LIGHT_GRAY)
        )
      )
    )
  )

  val cornellBoxBalls: Renderable = new PathTracer(
    camera = camera,
    samplesPerPixel = 10,

    scene = RayTracingScene(
      shapes = List(
        // WALLS
        Plane(
          point = Vec3(-15, 0, 0),
          normal = UNIT_X,
          material = Dielectric(RED)
        ),
        Plane(
          point = Vec3(15, 0, 0),
          normal = UNIT_X,
          material = Dielectric(LIME)
        ),
        Plane(
          point = Vec3(0, 0, -1),
          normal = UNIT_Z,
          material = Dielectric(DEEP_SKY_BLUE)
        ),
        Plane(
          point = Vec3(0, 0, 50),
          normal = UNIT_Z,
          material = Dielectric(DEEP_SKY_BLUE)
        ),
        Plane(
          point = Vec3(0, 15, 0),
          normal = UNIT_Y,
          material = Dielectric(WHITE)
        ),
        Plane(
          point = Vec3(0, -15, 0),
          normal = UNIT_Y,
          material = Dielectric(WHITE)
        ),

        // LIGHT SOURCE
        Sphere(
          center = Vec3(0, 20, 30),
          radius = 8,
          material = Dielectric(color = GRAY, emission = WHITE * 15)
        ),

        // OBJECTS
        Sphere(
          center = Vec3(-8, -8, 30),
          radius = 7,
          material = Dielectric(AQUAMARINE)
        ),
        Sphere(
          center = Vec3(6, -6, 40),
          radius = 9,
          material = Dielectric(TAN)
        ),
      )
    )
  )

  val pathTracingRayMarchingTest = new PathTracer(
    camera = camera,
    samplesPerPixel = 10,

    scene = RayMarchingScene(
      maxDist = 500,

      shapes = List(
        Torus(
          mainRadius = 10,
          tubeRadius = 3,
          transformation = new Transformation(-30, 0, -30, -2, 0, 30),
          material = Dielectric(GOLD)
        ),
        Sphere(
          center = Vec3(10, 3, 30),
          radius = 6,
          material = Dielectric(MEDIUM_BLUE, reflectivity = 0.2)
        ),
        Plane(
          point = Vec3(0, -10, 0),
          material = Dielectric(AQUAMARINE)
        ),

        // LIGHT SOURCES
        Sphere(
          center = Vec3(0, 20, 30),
          radius = 6,
          material = Dielectric(color = GRAY, emission = WHEAT * 15)
        ),
        Sphere(
          center = Vec3(35, 5, 30),
          radius = 6,
          material = Dielectric(color = GRAY, emission = WHEAT * 15)
        )
      )
    )
  )
}
