/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2013-2015, Gary Keorkunian                                       **
**                                                                      **
\*                                                                      */

package squants.electro

import squants._
import scala.util.Try

/**
 * @author  garyKeorkunian
 * @since   0.1
 *
 * @param value value in [[squants.electro.Farads]]
 */
final class Capacitance private (val value: Double, val unit: CapacitanceUnit)
  extends Quantity[Capacitance] {

  def dimension = Capacitance

  def *(that: ElectricPotential): ElectricCharge = Coulombs(this.toFarads * that.toVolts)
  def /(that: Length): Permittivity = FaradsPerMeter(this.toFarads / that.toMeters)

  def toFarads: Double = to(Farads)
  def toPicofarads: Double = to(Picofarads)
  def toNanofarads: Double = to(Nanofarads)
  def toMicrofarads: Double = to(Microfarads)
  def toMillifarads: Double = to(Millifarads)
  def toKilofarads: Double = to(Kilofarads)
}

object Capacitance extends Dimension[Capacitance] {
  private[electro] def apply[A](n: A, unit: CapacitanceUnit)(using num: Numeric[A]) = new Capacitance(num.toDouble(n), unit)
  def apply(value: Any): Try[Capacitance] = parse(value)
  def name = "Capacitance"
  def primaryUnit = Farads
  def siUnit = Farads
  def units: Set[UnitOfMeasure[Capacitance]] = Set(Farads, Picofarads, Nanofarads, Microfarads, Millifarads, Kilofarads)
}

trait CapacitanceUnit extends UnitOfMeasure[Capacitance] with UnitConverter {
  def apply[A](n: A)(using num: Numeric[A]): Capacitance = Capacitance(n, this)
}

object Farads extends CapacitanceUnit with PrimaryUnit with SiUnit {
  val symbol = "F"
}

object Picofarads extends CapacitanceUnit with SiUnit {
  val symbol = "pF"
  val conversionFactor = MetricSystem.Pico
}

object Nanofarads extends CapacitanceUnit with SiUnit {
  val symbol = "nF"
  val conversionFactor = MetricSystem.Nano
}

object Microfarads extends CapacitanceUnit with SiUnit {
  val symbol = "μF"
  val conversionFactor = MetricSystem.Micro
}

object Millifarads extends CapacitanceUnit with SiUnit {
  val symbol = "mF"
  val conversionFactor = MetricSystem.Milli
}

object Kilofarads extends CapacitanceUnit with SiUnit {
  val symbol = "kF"
  val conversionFactor = MetricSystem.Kilo
}

object CapacitanceConversions {
  lazy val farad: Capacitance = Farads(1)
  lazy val picofarad: Capacitance = Picofarads(1)
  lazy val nanofarad: Capacitance = Nanofarads(1)
  lazy val microfarad: Capacitance = Microfarads(1)
  lazy val millifarad: Capacitance = Millifarads(1)
  lazy val kilofarad: Capacitance = Kilofarads(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def farads: Capacitance = Farads(n)
    def picofarads: Capacitance = Picofarads(n)
    def nanofarads: Capacitance = Nanofarads(n)
    def microfarads: Capacitance = Microfarads(n)
    def millifarads: Capacitance = Millifarads(n)
    def kilofarads: Capacitance = Kilofarads(n)
  }

  given CapacitanceNumeric: AbstractQuantityNumeric[Capacitance](Capacitance.primaryUnit) {}
}
