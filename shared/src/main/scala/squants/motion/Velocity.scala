/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2013-2015, Gary Keorkunian                                       **
**                                                                      **
\*                                                                      */

package squants.motion

import squants.mass.Mass
import squants.space._
import squants.time._
import squants.{ AbstractQuantityNumeric, Dimension, PrimaryUnit, Quantity, SiUnit, UnitConverter, UnitOfMeasure }
import scala.util.Try

/**
 * Represents a quantify of Velocity
 *
 * @author  garyKeorkunian
 * @since   0.1
 *
 * @param value Double
 */
final class Velocity private (val value: Double, val unit: VelocityUnit)
  extends Quantity[Velocity]
  with TimeIntegral[Acceleration]
  with SecondTimeIntegral[Jerk]
  with TimeDerivative[Length] {

  def dimension = Velocity

  def timeDerived: Acceleration = MetersPerSecondSquared(toMetersPerSecond)
  protected[squants] def timeIntegrated: Length = Meters(toMetersPerSecond)
  protected[squants] def time: squants.Time = Seconds(1)

  def *(that: Mass): Momentum = NewtonSeconds(this.toMetersPerSecond * that.toKilograms)

  def /(that: TimeSquared): Jerk = this / that.time1 / that.time2
  def /(that: Jerk): TimeSquared = (this / that.timeIntegrated) * this.time

  def toFeetPerSecond: Double = to(FeetPerSecond)
  def toMillimetersPerSecond: Double = to(MillimetersPerSecond)
  def toMetersPerSecond: Double = to(MetersPerSecond)
  def toKilometersPerSecond: Double = to(KilometersPerSecond)
  def toEarthEscapeVelocities: Double = to(EarthEscapeVelocities)
  def toKilometersPerHour: Double = to(KilometersPerHour)
  def toUsMilesPerHour: Double = to(UsMilesPerHour)
  def toInternationalMilesPerHour: Double = to(InternationalMilesPerHour)
  def toKnots: Double = to(Knots)
}

object Velocity extends Dimension[Velocity] {
  private[motion] def apply[A](n: A, unit: VelocityUnit)(using num: Numeric[A]) = new Velocity(num.toDouble(n), unit)
  def apply(l: Length, t: Time): Velocity = MetersPerSecond(l.toMeters / t.toSeconds)
  def apply(value: Any): Try[Velocity] = parse(value)
  def name = "Velocity"
  def primaryUnit = MetersPerSecond
  def siUnit = MetersPerSecond
  def units: Set[UnitOfMeasure[Velocity]] = Set(MetersPerSecond, EarthEscapeVelocities, FeetPerSecond, MillimetersPerSecond, KilometersPerSecond, KilometersPerHour,
    UsMilesPerHour, InternationalMilesPerHour, Knots)
}

trait VelocityUnit extends UnitOfMeasure[Velocity] with UnitConverter {
  def apply[A](n: A)(using num: Numeric[A]): Velocity = Velocity(n, this)
}

object FeetPerSecond extends VelocityUnit {
  val symbol = "ft/s"
  val conversionFactor: Double = Feet.conversionFactor / Meters.conversionFactor
}

object MillimetersPerSecond extends VelocityUnit with SiUnit {
  val symbol = "mm/s"
  val conversionFactor: Double = Millimeters.conversionFactor / Meters.conversionFactor
}

object MetersPerSecond extends VelocityUnit with PrimaryUnit with SiUnit {
  val symbol = "m/s"
}

object KilometersPerSecond extends VelocityUnit with SiUnit {
  val symbol = "km/s"
  val conversionFactor: Double = Kilometers.conversionFactor / Meters.conversionFactor
}

object EarthEscapeVelocities extends VelocityUnit {
  val symbol = "Vₑ🜨"
  val conversionFactor: Double = 11186 * MetersPerSecond.conversionFactor
}

object KilometersPerHour extends VelocityUnit {
  val symbol = "km/h"
  val conversionFactor: Double = (Kilometers.conversionFactor / Meters.conversionFactor) / Time.SecondsPerHour
}

object UsMilesPerHour extends VelocityUnit {
  val symbol = "mph"
  val conversionFactor: Double = (UsMiles.conversionFactor / Meters.conversionFactor) / Time.SecondsPerHour
}

object InternationalMilesPerHour extends VelocityUnit {
  val symbol = "imph"
  val conversionFactor: Double = (InternationalMiles.conversionFactor / Meters.conversionFactor) / Time.SecondsPerHour
}

object Knots extends VelocityUnit {
  val symbol = "kn"
  val conversionFactor: Double = (NauticalMiles.conversionFactor / Meters.conversionFactor) / Time.SecondsPerHour
}

object VelocityConversions {
  lazy val footPerSecond: Velocity = FeetPerSecond(1)
  lazy val millimeterPerSecond: Velocity = MillimetersPerSecond(1)
  lazy val meterPerSecond: Velocity = MetersPerSecond(1)
  lazy val kilometerPerSecond: Velocity = KilometersPerSecond(1)
  lazy val kilometerPerHour: Velocity = KilometersPerHour(1)
  lazy val milePerHour: Velocity = UsMilesPerHour(1)
  lazy val knot: Velocity = Knots(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def fps: Velocity = FeetPerSecond(n)
    def mps: Velocity = MetersPerSecond(n)
    def kps: Velocity = KilometersPerSecond(n)
    def kph: Velocity = KilometersPerHour(n)
    def mph: Velocity = UsMilesPerHour(n)
    def knots: Velocity = Knots(n)
  }

  given VelocityNumeric: AbstractQuantityNumeric[Velocity](Velocity.primaryUnit) {}
}
