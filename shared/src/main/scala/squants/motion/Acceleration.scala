/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2013-2015, Gary Keorkunian                                       **
**                                                                      **
\*                                                                      */

package squants.motion

import squants.{ AbstractQuantityNumeric, Dimension, PrimaryUnit, Quantity, SiUnit, UnitConverter, UnitOfMeasure }
import squants.mass.Mass
import squants.space.{ Area, Feet, Length, Meters, Millimeters, UsMiles }
import squants.time.{ SecondTimeDerivative, Seconds, Time, TimeDerivative, TimeIntegral, TimeSquared }
import scala.util.Try

/**
 * Represents a quantity of acceleration
 *
 * @author  garyKeorkunian
 * @since   0.1
 *
 * @param value Double
 */
final class Acceleration private (val value: Double, val unit: AccelerationUnit)
  extends Quantity[Acceleration]
  with TimeDerivative[Velocity]
  with SecondTimeDerivative[Length]
  with TimeIntegral[Jerk] {

  def dimension = Acceleration

  protected[squants] def timeIntegrated: Velocity = MetersPerSecond(toMetersPerSecondSquared)
  protected[squants] def timeDerived: Jerk = MetersPerSecondCubed(toMetersPerSecondSquared)
  protected[squants] def time: Time = Seconds(1)

  def *(that: Mass): Force = Newtons(this.toMetersPerSecondSquared * that.toKilograms)
  def *(that: TimeSquared): Length = this * that.time1 * that.time2
  /** The gravitational parameter that gives this acceleration at a distance whose square is `that`: `mu = a * r²`. */
  def *(that: Area): GravitationalParameter = CubicMetersPerSecondSquared(this.toMetersPerSecondSquared * that.toSquareMeters)

  def toFeetPerSecondSquared: Double = to(FeetPerSecondSquared)
  def toMillimetersPerSecondSquared: Double = to(MillimetersPerSecondSquared)
  def toMetersPerSecondSquared: Double = to(MetersPerSecondSquared)
  def toUsMilesPerHourSquared: Double = to(UsMilesPerHourSquared)
  def toEarthGravities: Double = to(EarthGravities)

  def analyze(distance: Length): (Time, Velocity) = {
    val timeToDistance = (distance * 2 / this).squareRoot
    val finalVelocity = this * timeToDistance
    (timeToDistance, finalVelocity)
  }
  def analyze(accelerationTime: Time): (Length, Velocity) = {
    val finalVelocity = this * accelerationTime
    val distance = this * accelerationTime.squared * 0.5
    (distance, finalVelocity)
  }
  def analyze(velocity: Velocity): (Time, Length) = {
    val timeToVelocity = velocity / this
    val distance = this * timeToVelocity.squared * 0.5
    (timeToVelocity, distance)
  }
}

object Acceleration extends Dimension[Acceleration] {
  private[motion] def apply[A](n: A, unit: AccelerationUnit)(using num: Numeric[A]) = new Acceleration(num.toDouble(n), unit)
  def apply(value: Any): Try[Acceleration] = parse(value)
  def name = "Acceleration"
  def primaryUnit = MetersPerSecondSquared
  def siUnit = MetersPerSecondSquared
  def units: Set[UnitOfMeasure[Acceleration]] = Set(FeetPerSecondSquared, MillimetersPerSecondSquared, MetersPerSecondSquared, UsMilesPerHourSquared,
    EarthGravities)
}

/**
 * Base trait for units of [[squants.motion.Acceleration]]
 *
 * @author  garyKeorkunian
 * @since   0.1
 *
 */
trait AccelerationUnit extends UnitOfMeasure[Acceleration] with UnitConverter {
  def apply[A](n: A)(using num: Numeric[A]): Acceleration = Acceleration(n, this)
}

object MillimetersPerSecondSquared extends AccelerationUnit with SiUnit {
  val symbol = "mm/s²"
  val conversionFactor: Double = Millimeters.conversionFactor / Meters.conversionFactor
}

object MetersPerSecondSquared extends AccelerationUnit with PrimaryUnit with SiUnit {
  val symbol = "m/s²"
}

object FeetPerSecondSquared extends AccelerationUnit {
  val symbol = "ft/s²"
  val conversionFactor: Double = Feet.conversionFactor / Meters.conversionFactor
}

object UsMilesPerHourSquared extends AccelerationUnit {
  val symbol = "mph²"
  val conversionFactor: Double = (UsMiles.conversionFactor / Meters.conversionFactor) / math.pow(Time.SecondsPerHour, 2)
}

/**
 * Represents acceleration in Earth gravities also knows as G-Force, or g's
 */
object EarthGravities extends AccelerationUnit {
  val symbol = "g"
  val conversionFactor: Double = 9.80665 * Meters.conversionFactor
}

object AccelerationConversions {

  extension [A](n: A)(using num: Numeric[A]) {
    def mpss: Acceleration = MetersPerSecondSquared(n)
    def fpss: Acceleration = FeetPerSecondSquared(n)
  }

  given AccelerationNumeric: AbstractQuantityNumeric[Acceleration](Acceleration.primaryUnit) {}
}
