package squants.motion

import squants._
import squants.energy.{Grays, SpecificEnergy}
import squants.space.{CubicMeters, SquareMeters}
import squants.time.TimeSquared

/**
 * The gravitational parameter of a body, mu = G * M, in cubic metres per second squared.
 *
 * With a radius or distance it gives the quantities of orbital mechanics: the surface gravity `mu / r²`, the specific
 * orbital energy `mu / r` (and so the escape velocity `sqrt(2 * mu / r)`), and the orbital period via `a³ / mu`.
 *
 * @param value value in [[squants.motion.CubicMetersPerSecondSquared]]
 */
final class GravitationalParameter private (val value: Double, val unit: GravitationalParameterUnit)
  extends Quantity[GravitationalParameter] {

  def dimension = GravitationalParameter

  /** The acceleration at a distance whose square is `that`: `mu / r²`. */
  def /(that: Area): Acceleration = MetersPerSecondSquared(toCubicMetersPerSecondSquared / that.toSquareMeters)
  /** The area (a squared distance) at which the acceleration is `that`. */
  def /(that: Acceleration): Area = SquareMeters(toCubicMetersPerSecondSquared / that.toMetersPerSecondSquared)
  /** The specific energy at a distance: `mu / r`. */
  def /(that: Length): SpecificEnergy = Grays(toCubicMetersPerSecondSquared / that.toMeters)
  /** The volume (a cubed distance) whose period squared is `that`: `mu * T²`. */
  def *(that: TimeSquared): Volume = CubicMeters(toCubicMetersPerSecondSquared * that.time1.toSeconds * that.time2.toSeconds)

  def toCubicMetersPerSecondSquared = to(CubicMetersPerSecondSquared)
  def toCubicKilometersPerSecondSquared = to(CubicKilometersPerSecondSquared)
}

object GravitationalParameter extends Dimension[GravitationalParameter] {
  private[motion] def apply[A](n: A, unit: GravitationalParameterUnit)(implicit num: Numeric[A]) = new GravitationalParameter(num.toDouble(n), unit)
  def apply(value: Any) = parse(value)
  def name = "GravitationalParameter"
  def primaryUnit = CubicMetersPerSecondSquared
  def siUnit = CubicMetersPerSecondSquared
  def units = Set(CubicMetersPerSecondSquared, CubicKilometersPerSecondSquared)
}

trait GravitationalParameterUnit extends UnitOfMeasure[GravitationalParameter] with UnitConverter {
  def apply[A](n: A)(implicit num: Numeric[A]) = GravitationalParameter(n, this)
}

object CubicMetersPerSecondSquared extends GravitationalParameterUnit with PrimaryUnit with SiUnit {
  val symbol = "m³/s²"
}

object CubicKilometersPerSecondSquared extends GravitationalParameterUnit {
  val conversionFactor = MetricSystem.Giga // (1 km)³ = 1e9 m³
  val symbol = "km³/s²"
}

object GravitationalParameterConversions {
  lazy val cubicMeterPerSecondSquared = CubicMetersPerSecondSquared(1)
  lazy val cubicKilometerPerSecondSquared = CubicKilometersPerSecondSquared(1)

  implicit class GravitationalParameterConversions[A](n: A)(implicit num: Numeric[A]) {
    def cubicMetersPerSecondSquared = CubicMetersPerSecondSquared(n)
    def cubicKilometersPerSecondSquared = CubicKilometersPerSecondSquared(n)
  }

  implicit object GravitationalParameterNumeric extends AbstractQuantityNumeric[GravitationalParameter](GravitationalParameter.primaryUnit)
}
