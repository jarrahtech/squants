package squants

import squants.motion.{CubicMetersPerSecondSquared, GravitationalParameter}
import squants.thermal.{JoulesPerKelvin, ThermalCapacity}

/**
 * The gravitational constant G, in m³/(kg·s²). Multiplying it by a [[Mass]] gives that mass's gravitational parameter.
 */
final case class GravitationalConstant(value: Double) {
  def *(that: Mass): GravitationalParameter = CubicMetersPerSecondSquared(value * that.toKilograms)
}

/**
 * Physical constants (CODATA 2018 values).
 */
object PhysicalConstants {
  /** The gravitational constant, 6.67430e-11 m³/(kg·s²). */
  val G: GravitationalConstant = GravitationalConstant(6.67430e-11)

  /** The Stefan-Boltzmann constant, 5.670374419e-8 W/(m²·K⁴). */
  val StefanBoltzmann: Double = 5.670374419e-8

  /** The Boltzmann constant, 1.380649e-23 J/K (exact since the 2019 SI redefinition). */
  val Boltzmann: ThermalCapacity = JoulesPerKelvin(1.380649e-23)
}
