/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2013-2015, Gary Keorkunian                                       **
**                                                                      **
\*                                                                      */

package squants.electro

import squants._
import squants.energy.Watts
import squants.time.{ Seconds, TimeDerivative }
import scala.util.Try

/**
 * Represents a quantity of electric current
 *
 * @author  garyKeorkunian
 * @since   0.1
 *
 * @param value the amount of charge in [[squants.electro.Amperes]]'s
 */
final class ElectricCurrent private (val value: Double, val unit: ElectricCurrentUnit)
  extends Quantity[ElectricCurrent]
  with TimeDerivative[ElectricCharge] {

  def dimension = ElectricCurrent

  protected[squants] def timeIntegrated: ElectricCharge = Coulombs(toAmperes)
  protected[squants] def time: Time = Seconds(1)

  def *(that: ElectricalResistance): ElectricPotential = Volts(this.toAmperes * that.toOhms)
  def *(that: ElectricPotential): Power = Watts(this.toAmperes * that.toVolts)
  def *(that: Inductance): MagneticFlux = Webers(this.toAmperes * that.toHenry)
  def /(that: ElectricPotential): ElectricalConductance = Siemens(this.toAmperes / that.toVolts)
  def /(that: Length): MagneticFieldStrength = AmperesPerMeter(this.toAmperes / that.toMeters)
  def /(that: Area): ElectricCurrentDensity = AmperesPerSquareMeter(this.toAmperes / that.toSquareMeters)

  def toAmperes: Double = to(Amperes)
  def toMilliamperes: Double = to(Milliamperes)
}

object ElectricCurrent extends Dimension[ElectricCurrent] with BaseDimension {
  private[electro] def apply[A](n: A, unit: ElectricCurrentUnit)(using num: Numeric[A]) = new ElectricCurrent(num.toDouble(n), unit)
  def apply(value: Any): Try[ElectricCurrent] = parse(value)
  def name = "ElectricCurrent"
  def primaryUnit = Amperes
  def siUnit = Amperes
  def units: Set[UnitOfMeasure[ElectricCurrent]] = Set(Amperes, Milliamperes)
  def dimensionSymbol = "I"
}

/**
 * Base trait for units of [[squants.electro.ElectricCurrent]]
 */
trait ElectricCurrentUnit extends UnitOfMeasure[ElectricCurrent] with UnitConverter {
  def apply[A](n: A)(using num: Numeric[A]): ElectricCurrent = ElectricCurrent(n, this)
}

/**
 * Amperes
 */
object Amperes extends ElectricCurrentUnit with PrimaryUnit with SiBaseUnit {
  val symbol = "A"
}

/**
 * Milliamperes
 */
object Milliamperes extends ElectricCurrentUnit with SiUnit {
  val symbol = "mA"
  val conversionFactor = MetricSystem.Milli
}

object ElectricCurrentConversions {
  lazy val ampere: ElectricCurrent = Amperes(1)
  lazy val amp: ElectricCurrent = Amperes(1)
  lazy val milliampere: ElectricCurrent = Milliamperes(1)
  lazy val milliamp: ElectricCurrent = Milliamperes(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def amperes: ElectricCurrent = Amperes(n)
    def amps: ElectricCurrent = Amperes(n)
    def A: ElectricCurrent = Amperes(n)
    def milliampers: ElectricCurrent = Milliamperes(n)
    def milliamps: ElectricCurrent = Milliamperes(n)
    def mA: ElectricCurrent = Milliamperes(n)
  }

  given ElectricCurrentNumeric: AbstractQuantityNumeric[ElectricCurrent](ElectricCurrent.primaryUnit) {}
}
