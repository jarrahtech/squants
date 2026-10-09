/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2013-2015, Gary Keorkunian                                       **
**                                                                      **
\*                                                                      */

package squants.energy

import squants._
import squants.electro.{ Amperes, ElectricCurrent, ElectricPotential, Volts }
import squants.radio.{ Irradiance, RadiantIntensity, SpectralPower, WattsPerMeter, WattsPerSquareMeter, WattsPerSteradian }
import squants.space.{ SolidAngle, SquareMeters, SquaredRadians }
import squants.time.{ Hours, TimeDerivative, TimeIntegral }
import scala.util.Try

/**
 * Represents a quantity of power / load, the rate at which energy produced or used
 *
 * The first time derivative of [[squants.energy.Energy]]
 *
 * @author  garyKeorkunian
 * @since   0.1
 *
 * @param value value in [[squants.energy.Watts]]
 */
final class Power private (val value: Double, val unit: PowerUnit)
  extends Quantity[Power]
  with TimeDerivative[Energy]
  with TimeIntegral[PowerRamp] {

  def dimension = Power

  protected[squants] def timeIntegrated: Energy = WattHours(toWatts)
  protected def timeDerived: PowerRamp = WattsPerHour(toWatts)
  protected[squants] def time: Time = Hours(1)

  def /(that: Length): SpectralPower = WattsPerMeter(this.toWatts / that.toMeters)
  def /(that: SpectralPower): Length = Meters(this.toWatts / that.toWattsPerMeter)
  def /(that: Area): Irradiance = WattsPerSquareMeter(this.toWatts / that.toSquareMeters)
  def /(that: Irradiance): Area = SquareMeters(this.toWatts / that.toWattsPerSquareMeter)
  def /(that: RadiantIntensity): SolidAngle = SquaredRadians(this.toWatts / that.toWattsPerSteradian)
  def /(that: SolidAngle): RadiantIntensity = WattsPerSteradian(this.toWatts / that.toSteradians)
  def /(that: ElectricPotential): ElectricCurrent = Amperes(this.toWatts / that.toVolts)
  def /(that: ElectricCurrent): ElectricPotential = Volts(this.toWatts / that.toAmperes)
  def /(that: Volume): PowerDensity = WattsPerCubicMeter(this.toWatts / that.toCubicMeters)

  def toMilliwatts: Double = to(Milliwatts)
  def toWatts: Double = to(Watts)
  def toKilowatts: Double = to(Kilowatts)
  def toMegawatts: Double = to(Megawatts)
  def toGigawatts: Double = to(Gigawatts)
  def toBtusPerHour: Double = to(BtusPerHour)
  def toErgsPerSecond: Double = to(ErgsPerSecond)
  def toSolarLuminosities: Double = to(SolarLuminosities)
}

/**
 * Companion object for [[squants.energy.Power]]
 */
object Power extends Dimension[Power] {
  private[energy] def apply[A](n: A, unit: PowerUnit)(using num: Numeric[A]) = new Power(num.toDouble(n), unit)
  def apply(energy: Energy, time: Time): Power = apply(energy.toWattHours / time.toHours, Watts)
  def apply(value: Any): Try[Power] = parse(value)

  def name = "Power"
  def primaryUnit = Watts
  def siUnit = Watts
  def units: Set[UnitOfMeasure[Power]] = Set(Watts, Milliwatts, Kilowatts, Megawatts, Gigawatts, BtusPerHour, ErgsPerSecond, SolarLuminosities)
}

trait PowerUnit extends UnitOfMeasure[Power] with UnitConverter {
  def apply[A](n: A)(using num: Numeric[A]): Power = Power(n, this)
}

object Milliwatts extends PowerUnit with SiUnit {
  val conversionFactor = MetricSystem.Milli
  val symbol = "mW"
}

object Watts extends PowerUnit with PrimaryUnit with SiUnit {
  val symbol = "W"
}

object Kilowatts extends PowerUnit with SiUnit {
  val conversionFactor = MetricSystem.Kilo
  val symbol = "kW"
}

object Megawatts extends PowerUnit with SiUnit {
  val conversionFactor = MetricSystem.Mega
  val symbol = "MW"
}

object Gigawatts extends PowerUnit with SiUnit {
  val conversionFactor = MetricSystem.Giga
  val symbol = "GW"
}

object BtusPerHour extends PowerUnit {
  val conversionFactor = EnergyConversions.btuMultiplier
  val symbol = "Btu/hr"
}

object ErgsPerSecond extends PowerUnit {
  val conversionFactor = 1e-7
  val symbol: String = Ergs.symbol + "/" + Seconds.symbol
}

object SolarLuminosities extends PowerUnit {
  val conversionFactor = 3.828e26
  val symbol = "L☉"
}

object PowerConversions {
  lazy val milliwatt: Power = Milliwatts(1)
  lazy val mW = milliwatt
  lazy val watt: Power = Watts(1)
  lazy val W = watt
  lazy val kilowatt: Power = Kilowatts(1)
  lazy val kW = kilowatt
  lazy val megawatt: Power = Megawatts(1)
  lazy val MW = megawatt
  lazy val gigawatt: Power = Gigawatts(1)
  lazy val GW = gigawatt
  lazy val solarLuminosity: Power = SolarLuminosities(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def mW: Power = Milliwatts(n)
    def W: Power = Watts(n)
    def kW: Power = Kilowatts(n)
    def MW: Power = Megawatts(n)
    def GW: Power = Gigawatts(n)
    def milliwatts: Power = Milliwatts(n)
    def watts: Power = Watts(n)
    def kilowatts: Power = Kilowatts(n)
    def megawatts: Power = Megawatts(n)
    def gigawatts: Power = Gigawatts(n)
    def BTUph: Power = BtusPerHour(n)
    def ergsPerSecond: Power = ErgsPerSecond(n)
    def solarLuminosities: Power = SolarLuminosities(n)
  }

  extension (s: String) {
    def toPower: Try[Power] = Power(s)
  }

  given PowerNumeric: AbstractQuantityNumeric[Power](Power.primaryUnit) {}
}
