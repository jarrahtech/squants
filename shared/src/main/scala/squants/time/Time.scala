/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2013-2015, Gary Keorkunian                                       **
**                                                                      **
\*                                                                      */

package squants.time

import squants._
import squants.space.Area
import squants.radio.{ AreaTime, SquareMeterSeconds }

import scala.concurrent.duration.{ DAYS, Duration, HOURS, MICROSECONDS, MILLISECONDS, MINUTES, NANOSECONDS, SECONDS }
import scala.util.Try

/**
 * Represents a quantity of Time
 *
 * @author  garyKeorkunian
 * @since   0.1
 *
 * @param value value in [[squants.time.Milliseconds]]
 */
final class Time private (val value: Double, val unit: TimeUnit)
  extends Quantity[Time] {

  def dimension = Time

  def millis = toMilliseconds.toLong

  def *[A <: squants.Quantity[A] & squants.time.TimeIntegral[?]](that: TimeDerivative[A]): A = that * this

  def *(that: Time): TimeSquared = TimeSquared(this, that)
  def squared: TimeSquared = TimeSquared(this)
  def *(that: Area): AreaTime = SquareMeterSeconds(this.toSeconds * that.toSquareMeters)

  def toNanoseconds: Double = to(Nanoseconds)
  def toMicroseconds: Double = to(Microseconds)
  def toMilliseconds: Double = to(Milliseconds)
  def toSeconds: Double = to(Seconds)
  def toMinutes: Double = to(Minutes)
  def toHours: Double = to(Hours)
  def toDays: Double = to(Days)
  def toEarthYears: Double = to(EarthYears)
  def toEarthMegaYears: Double = to(EarthMegaYears)
  def toEarthGigaYears: Double = to(EarthGigaYears)
}

object Time extends Dimension[Time] with BaseDimension {
  val NanosecondsPerSecond = 1.0e9
  val MicrosecondsPerSecond = 1.0e6
  val MillisecondsPerNanosecond = 1.0e-6
  val MillisecondsPerMicrosecond = 1.0e-3
  val MillisecondsPerSecond = 1e3
  val MillisecondsPerMinute: Double = MillisecondsPerSecond * 60d
  val MillisecondsPerHour: Double = MillisecondsPerMinute * 60d
  val MillisecondsPerDay: Double = MillisecondsPerHour * 24d
  val SecondsPerMinute = 60d
  val SecondsPerHour: Double = SecondsPerMinute * 60d
  val SecondsPerDay: Double = SecondsPerHour * 24
  val MinutesPerHour = 60d
  val HoursPerDay = 24d

  private[time] def apply[A](n: A, unit: TimeUnit)(using num: Numeric[A]) = new Time(num.toDouble(n), unit)
  def apply(value: Any): Try[Time] = parse(value)
  def apply(duration: Duration): Time = duration.unit match {
    case NANOSECONDS => Nanoseconds(duration.length)
    case MICROSECONDS => Microseconds(duration.length)
    case MILLISECONDS => Milliseconds(duration.length)
    case SECONDS => Seconds(duration.length)
    case MINUTES => Minutes(duration.length)
    case HOURS => Hours(duration.length)
    case DAYS => Days(duration.length)
  }

  def name = "Time"
  def primaryUnit = Milliseconds
  def siUnit = Seconds
  def units: Set[UnitOfMeasure[Time]] = Set(Nanoseconds, Microseconds, Milliseconds, Seconds, Minutes, Hours, Days,
    EarthYears, EarthMegaYears, EarthGigaYears)
  def dimensionSymbol = "T"
}

trait TimeUnit extends UnitOfMeasure[Time] with UnitConverter {
  def apply[A](n: A)(using num: Numeric[A]): Time = Time(n, this)
}

object Nanoseconds extends TimeUnit with SiUnit {
  val conversionFactor: Double = Milliseconds.conversionFactor / Time.MicrosecondsPerSecond
  val symbol = "ns"
}

object Microseconds extends TimeUnit with SiUnit {
  val conversionFactor: Double = Milliseconds.conversionFactor / Time.MillisecondsPerSecond
  val symbol = "µs"
}

object Milliseconds extends TimeUnit with PrimaryUnit with SiUnit {
  val symbol = "ms"
}

object Seconds extends TimeUnit with SiBaseUnit {
  val conversionFactor: Double = Milliseconds.conversionFactor * Time.MillisecondsPerSecond
  val symbol = "s"
}

object Minutes extends TimeUnit {
  val conversionFactor: Double = Seconds.conversionFactor * Time.SecondsPerMinute
  val symbol = "min"
}

object Hours extends TimeUnit {
  val conversionFactor: Double = Minutes.conversionFactor * Time.MinutesPerHour
  val symbol = "h"
}

object Days extends TimeUnit {
  val conversionFactor: Double = Hours.conversionFactor * Time.HoursPerDay
  val symbol = "d"
}

object EarthYears extends TimeUnit {
  val conversionFactor: Double = Days.conversionFactor * 365.2421897
  val symbol = "Y🜨"
}

object EarthMegaYears extends TimeUnit {
  val conversionFactor: Double = EarthYears.conversionFactor * MetricSystem.Mega
  val symbol = "MY🜨"
}

object EarthGigaYears extends TimeUnit {
  val conversionFactor: Double = EarthYears.conversionFactor * MetricSystem.Giga
  val symbol = "BY🜨"
}

object TimeConversions {
  lazy val nanosecond: Time = Nanoseconds(1)
  lazy val microsecond: Time = Microseconds(1)
  lazy val millisecond: Time = Milliseconds(1)
  lazy val second: Time = Seconds(1)
  lazy val minute: Time = Minutes(1)
  lazy val halfHour: Time = Minutes(30)
  lazy val hour: Time = Hours(1)
  lazy val day: Time = Days(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def nanoseconds: Time = Nanoseconds(n)
    def microseconds: Time = Microseconds(n)
    def milliseconds: Time = Milliseconds(n)
    def seconds: Time = Seconds(n)
    def minutes: Time = Minutes(n)
    def hours: Time = Hours(n)
    def days: Time = Days(n)
  }

  extension (s: String) {
    def toTime: Try[Time] = Time(s)
  }

  given TimeNumeric: AbstractQuantityNumeric[Time](Time.primaryUnit) {}

  /**
   * Converts a Squants Time to Scala Duration
   *
   * A whole value keeps its unit (`Seconds(90)` is 90 seconds). A fractional value is kept to the nanosecond
   * (`Seconds(1.5)` is 1500 milliseconds). An infinite Time gives an infinite Duration and NaN gives Duration.Undefined.
   *
   * @throws java.lang.IllegalArgumentException for a finite Time beyond what a Duration can hold (about 292 years)
   */
  given timeToScalaDuration: Conversion[Time, Duration] = time => time.unit match {
    case Nanoseconds => toDuration(time.value, NANOSECONDS)
    case Microseconds => toDuration(time.value, MICROSECONDS)
    case Milliseconds => toDuration(time.value, MILLISECONDS)
    case Seconds => toDuration(time.value, SECONDS)
    case Minutes => toDuration(time.value, MINUTES)
    case Hours => toDuration(time.value, HOURS)
    case Days => toDuration(time.value, DAYS)
    case _ => toDuration(time.toDays, DAYS) // units Duration has no equivalent for, such as EarthYears
  }

  private def toDuration(value: Double, unit: java.util.concurrent.TimeUnit): Duration =
    if (value.isWhole) Duration(value.toLong, unit) else Duration(value, unit)

  given scalaDurationToTime: Conversion[Duration, Time] = Time(_)
}
