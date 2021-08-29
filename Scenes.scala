import Graphics3D._
import Graphics3D.Utils._
import Graphics3D.BaseObjects._
import Graphics3D.Colors._
import Graphics3D.Materials._
import Graphics3D.Shapes._

object Scenes {
  val test: RayMarchingScene = new RayMarchingScene(
    imageWidth = 600,
    imageHeight = 600,

    lights = List(Light(-14, 14, 0)),

    shapes = List(
      Cone(height = 15, radius = 7,
        pos = new Position(7, -15, 29),
        material = Matte(MEDIUM_BLUE)
      ),
      Sphere(
        center = Vec3(-5, 0, 22),
        radius = 5,
        material = Matte(LAWN_GREEN)
      ),
      Cylinder(height = 20, radius = 6,
        pos = new Position(-88.2, -36, 0, 4, 7, 39),
        material = Matte(DEEP_PINK)
      ),
      Box(lenX = 30, lenY = 30, lenZ = 50,
        pos = new Position(0, 0, 24),
        material = Matte(WHITE)
      )
    )
  )

  val sphereOnPlane: RayMarchingScene = new RayMarchingScene(
    imageWidth = 600,
    imageHeight = 600,

    lights = List(Light(20, 0, 0)),

    shapes = List(
      Plane(
        point = Vec3(0, -5, 0),
        normal = unitY,
        material = Matte(FIREBRICK)
      ),
      Cylinder(height = 10, radius = 3,
        pos = new Position(0, -5, 20),
        material = Matte(ORANGE_RED)
      )
    )
  )
}
