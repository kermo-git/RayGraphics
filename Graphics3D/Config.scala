package Graphics3D

object Config {
  val MAX_RECURSION_DEPTH = 5
  val RAY_HIT_BIAS = 0.005
  val SHADOWS = true

  // Only for ray marching

  val MAX_DIST = 200.0
  val RAY_HIT_THRESHOLD = 0.001
  val SOFT_SHADOWS = false
  val GRADIENT_EPSILON = 0.001
}