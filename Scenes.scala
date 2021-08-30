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

  val insideBox = new RayMarchingScene(
    imageWidth = 600,
    imageHeight = 600,

    lights = List(Light(-14, 14, 0)),

    shapes = List(
      Cone(height = 15, radius = 7,
        pos = new Position(7, -15, 29),
        material = Matte(FIREBRICK)
      ),
      Sphere(
        center = Vec3(-5, 0, 22),
        radius = 5,
        material = Matte(ORANGE)
      ),
      Cylinder(height = 20, radius = 6,
        pos = new Position(-88.2, -36, 0, 4, 7, 39),
        material = Matte(ORANGE_RED)
      ),
      Box(lenX = 30, lenY = 30, lenZ = 50,
        pos = new Position(0, 0, 24),
        material = Matte(AZURE)
      )
    )
  )

  val torus = new RayMarchingScene(
    imageWidth = 600,
    imageHeight = 600,

    lights = List(Light(10, 8, 10)),
    softShadows = true,

    shapes = List(
      Plane(
        point = Vec3(0, -8, 0),
        normal = unitY,
        material = Matte(MEDIUM_VIOLET_RED)
      ),
      Torus(mainRadius = 10, tubeRadius = 3,
        pos = new Position(-30, 0, 0, 0, 3, 25),
        material = Matte(ORANGE_RED)
      )
    )
  )
}
