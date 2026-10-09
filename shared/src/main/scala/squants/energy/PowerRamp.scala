/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2013-2015, Gary Keorkunian                                       **
**                                                                      **
\*                                                                      */

package squants.energy

import squants.{ Time, _ }
import squants.time._
import scala.util.Try

/**
 * Represents the rate of change of [[squants.energy.Power]] over time
 *
 * @author  garyKeorkunian
 * @since   0.1
 *
 * @param value value in [[squants.energy.WattsPerHour]]
 */
final class PowerRamp private (val value: Double, val unit: PowerRampUnit)
  extends Quantity[PowerRamp]
  with TimeDerivative[Power]
  with SecondTimeDerivative[Energy] {

  def dimension = PowerRamp

  protected[squants] def timeIntegrated: Power = Watts(toWattsPerHour)
  protected[squants] def time: Time = Hours(1)

  def *(that: TimeSquared): Energy = this * that.time1 * that.time2

  def toWattsPerHour: Double = to(WattsPerHour)
  def toWattsPerMinutes: Double = to(WattsPerMinute)
  def toKilowattsPerHour: Double = to(KilowattsPerHour)
  def toKilowattsPerMinute: Double = to(KilowattsPerMinute)
  def toMegawattsPerHour: Double = to(MegawattsPerHour)
  def toGigawattsPerHour: Double = to(GigawattsPerHour)
}

object PowerRamp extends Dimension[PowerRamp] {
  private[energy] def apply[A](n: A, unit: PowerRampUnit)(using num: Numeric[A]) = new PowerRamp(num.toDouble(n), unit)
  def apply(change: Power, time: Time): PowerRamp = apply(change.toWatts / time.toHours, WattsPerHour)
  def apply(value: Any): Try[PowerRamp] = parse(value)
  def name = "PowerRamp"
  def primaryUnit = WattsPerHour
  def siUnit = WattsPerHour
  def units: Set[UnitOfMeasure[PowerRamp]] = Set(WattsPerHour, WattsPerMinute, KilowattsPerHour, KilowattsPerMinute, MegawattsPerHour, GigawattsPerHour)
}

trait PowerRampUnit extends UnitOfMeasure[PowerRamp] with UnitConverter {
  def apply[A](n: A)(using num: Numeric[A]): PowerRamp = PowerRamp(n, this)
}

object WattsPerHour extends PowerRampUnit with PrimaryUnit with SiUnit {
  val symbol = "W/h"
}

object WattsPerMinute extends PowerRampUnit with SiUnit {
  val conversionFactor: Double = WattsPerHour.conversionFactor / 60D
  val symbol = "W/m"
}

object KilowattsPerHour extends PowerRampUnit with SiUnit {
  val conversionFactor = MetricSystem.Kilo
  val symbol = "kW/h"
}

object KilowattsPerMinute extends PowerRampUnit with SiUnit {
  val conversionFactor: Double = KilowattsPerHour.conversionFactor / 60D
  val symbol = "kW/m"
}

object MegawattsPerHour extends PowerRampUnit with SiUnit {
  val conversionFactor = MetricSystem.Mega
  val symbol = "MW/h"
}

object GigawattsPerHour extends PowerRampUnit with SiUnit {
  val conversionFactor = MetricSystem.Giga
  val symbol = "GW/h"
}

object PowerRampConversions {
  lazy val wattPerHour: PowerRamp = WattsPerHour(1)
  lazy val Wph = wattPerHour
  lazy val wattPerMinute: PowerRamp = WattsPerMinute(1)
  lazy val Wpm = wattPerMinute
  lazy val kilowattPerHour: PowerRamp = KilowattsPerHour(1)
  lazy val kWph = kilowattPerHour
  lazy val kilowattPerMinute: PowerRamp = KilowattsPerMinute(1)
  lazy val kWpm = kilowattPerMinute
  lazy val megawattPerHour: PowerRamp = MegawattsPerHour(1)
  lazy val MWph = megawattPerHour
  lazy val gigawattPerHour: PowerRamp = GigawattsPerHour(1)
  lazy val GWph = gigawattPerHour

  extension [A](n: A)(using num: Numeric[A]) {
    def Wph: PowerRamp = WattsPerHour(n)
    def Wpm: PowerRamp = WattsPerMinute(n)
    def kWph: PowerRamp = KilowattsPerHour(n)
    def kWpm: PowerRamp = KilowattsPerMinute(n)
    def MWph: PowerRamp = MegawattsPerHour(n)
    def GWph: PowerRamp = GigawattsPerHour(n)
  }

  extension (s: String) {
    def toPowerRamp: Try[PowerRamp] = PowerRamp(s)
  }

  given PowerRampNumeric: AbstractQuantityNumeric[PowerRamp](PowerRamp.primaryUnit) {}
}
