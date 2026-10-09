/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2013-2015, Gary Keorkunian                                       **
**                                                                      **
\*                                                                      */

package squants.electro

import squants._
import squants.energy.{ Joules, Watts }
import squants.time.{ Seconds, TimeDerivative }
import scala.util.Try

/**
 * @author  garyKeorkunian
 * @since   0.1
 *
 * @param value value in [[squants.electro.Volts]]
 */
final class ElectricPotential private (val value: Double, val unit: ElectricPotentialUnit)
  extends Quantity[ElectricPotential]
  with TimeDerivative[MagneticFlux] {

  def dimension = ElectricPotential

  protected[squants] def timeIntegrated: MagneticFlux = Webers(toVolts)
  protected[squants] def time: Time = Seconds(1)

  def *(that: ElectricCurrent): Power = Watts(toVolts * that.toAmperes)
  def *(that: Capacitance): ElectricCharge = Coulombs(toVolts * that.toFarads)
  def *(that: ElectricCharge): Energy = Joules(toVolts * that.toCoulombs)

  def /(that: ElectricCurrent): ElectricalResistance = Ohms(this.toVolts / that.toAmperes)
  def /(that: ElectricalResistance): ElectricCurrent = Amperes(this.toVolts / that.toOhms)
  def /(that: Length): ElectricFieldStrength = VoltsPerMeter(this.toVolts / that.toMeters)

  def toVolts: Double = to(Volts)
  def toMicrovolts: Double = to(Microvolts)
  def toMillivolts: Double = to(Millivolts)
  def toKilovolts: Double = to(Kilovolts)
  def toMegavolts: Double = to(Megavolts)
}

object ElectricPotential extends Dimension[ElectricPotential] {
  private[electro] def apply[A](n: A, unit: ElectricPotentialUnit)(using num: Numeric[A]) = new ElectricPotential(num.toDouble(n), unit)
  def apply(value: Any): Try[ElectricPotential] = parse(value)
  def name = "ElectricPotential"
  def primaryUnit = Volts
  def siUnit = Volts
  def units: Set[UnitOfMeasure[ElectricPotential]] = Set(Volts, Microvolts, Millivolts, Kilovolts, Megavolts)
}

trait ElectricPotentialUnit extends UnitOfMeasure[ElectricPotential] with UnitConverter {
  def apply[A](n: A)(using num: Numeric[A]): ElectricPotential = ElectricPotential(n, this)
}

object Volts extends ElectricPotentialUnit with PrimaryUnit with SiUnit {
  val symbol = "V"
}

object Microvolts extends ElectricPotentialUnit with SiUnit {
  val symbol = "μV"
  val conversionFactor = MetricSystem.Micro
}

object Millivolts extends ElectricPotentialUnit with SiUnit {
  val symbol = "mV"
  val conversionFactor = MetricSystem.Milli
}

object Kilovolts extends ElectricPotentialUnit with SiUnit {
  val symbol = "kV"
  val conversionFactor = MetricSystem.Kilo
}

object Megavolts extends ElectricPotentialUnit with SiUnit {
  val symbol = "MV"
  val conversionFactor = MetricSystem.Mega
}

object ElectricPotentialConversions {
  lazy val volt: ElectricPotential = Volts(1)
  lazy val microvolt: ElectricPotential = Microvolts(1)
  lazy val millivolt: ElectricPotential = Millivolts(1)
  lazy val kilovolt: ElectricPotential = Kilovolts(1)
  lazy val megavolt: ElectricPotential = Megavolts(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def V: ElectricPotential = Volts(n)
    def volts: ElectricPotential = Volts(n)
    def microvolts: ElectricPotential = Microvolts(n)
    def millivolts: ElectricPotential = Millivolts(n)
    def kilovolts: ElectricPotential = Kilovolts(n)
    def megavolts: ElectricPotential = Megavolts(n)
  }

  given ElectricPotentialNumeric: AbstractQuantityNumeric[ElectricPotential](ElectricPotential.primaryUnit) {}
}

