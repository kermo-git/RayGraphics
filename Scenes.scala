import Graphics3D.BaseObjects.Light
import Graphics3D.Materials._
import Graphics3D.Shapes._
import Graphics3D._

object Scenes {
  val test: RayMarchingScene = new RayMarchingScene(
    lights = List(Light(-14, 14, 0)),
    shapes = List(
      new Cone(
        15, 7,
        new Position(7, -15, 29),
        new Metal(MEDIUM_BLUE)
      ),
      new Sphere(
        -5, 0, 22, 5,
        new Glass
      ),
      new Cylinder(
        20, 6,
        new Position(-88.2, -36, 0, 4, 7, 39),
        new Metal(DEEP_PINK)
      ),
      new Box(
        30, 30, 50,
        new Position(0, 0, 24),
        new Metal(WHITE)
      )
    )
  )
}
