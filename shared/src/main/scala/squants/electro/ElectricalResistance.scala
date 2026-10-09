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
 * @param value value in [[squants.electro.Ohms]]
 */
final class ElectricalResistance private (val value: Double, val unit: ElectricalResistanceUnit)
  extends Quantity[ElectricalResistance] {

  def dimension = ElectricalResistance

  def *(that: ElectricCurrent): ElectricPotential = Volts(this.toOhms * that.toAmperes)
  def *(that: Length): Resistivity = OhmMeters(this.toOhms * that.toMeters)

  def toOhms: Double = to(Ohms)
  def toNanohms: Double = to(Nanohms)
  def toMicrohms: Double = to(Microohms)
  def toMillohms: Double = to(Milliohms)
  def toKilohms: Double = to(Kilohms)
  def toMegohms: Double = to(Megohms)
  def toGigohms: Double = to(Gigohms)

  def inSiemens: ElectricalConductance = Siemens(1.0 / to(Ohms))
}

object ElectricalResistance extends Dimension[ElectricalResistance] {
  private[electro] def apply[A](n: A, unit: ElectricalResistanceUnit)(using num: Numeric[A]) = new ElectricalResistance(num.toDouble(n), unit)
  def apply(value: Any): Try[ElectricalResistance] = parse(value)
  def name = "ElectricalResistance"
  def primaryUnit = Ohms
  def siUnit = Ohms
  def units: Set[UnitOfMeasure[ElectricalResistance]] = Set(Ohms, Nanohms, Microohms, Milliohms, Kilohms, Megohms, Gigohms)
}

trait ElectricalResistanceUnit extends UnitOfMeasure[ElectricalResistance] with UnitConverter {
  def apply[A](n: A)(using num: Numeric[A]): ElectricalResistance = ElectricalResistance(n, this)
}

object Ohms extends ElectricalResistanceUnit with PrimaryUnit with SiUnit {
  val symbol = "Ω"
}

object Nanohms extends ElectricalResistanceUnit with SiUnit {
  val symbol = "nΩ"
  val conversionFactor = MetricSystem.Nano
}

object Microohms extends ElectricalResistanceUnit with SiUnit {
  val symbol = "µΩ"
  val conversionFactor = MetricSystem.Micro
}

object Milliohms extends ElectricalResistanceUnit with SiUnit {
  val symbol = "mΩ"
  val conversionFactor = MetricSystem.Milli
}

object Kilohms extends ElectricalResistanceUnit with SiUnit {
  val symbol = "kΩ"
  val conversionFactor = MetricSystem.Kilo
}

object Megohms extends ElectricalResistanceUnit with SiUnit {
  val symbol = "MΩ"
  val conversionFactor = MetricSystem.Mega
}

object Gigohms extends ElectricalResistanceUnit with SiUnit {
  val symbol = "GΩ"
  val conversionFactor = MetricSystem.Giga
}

object ElectricalResistanceConversions {
  lazy val ohm: ElectricalResistance = Ohms(1)
  lazy val nanohm: ElectricalResistance = Nanohms(1)
  lazy val microohm: ElectricalResistance = Microohms(1)
  lazy val milliohm: ElectricalResistance = Milliohms(1)
  lazy val kilohm: ElectricalResistance = Kilohms(1)
  lazy val megohm: ElectricalResistance = Megohms(1)
  lazy val gigohm: ElectricalResistance = Gigohms(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def ohms: ElectricalResistance = Ohms(n)
    def nanohms: ElectricalResistance = Nanohms(n)
    def microohms: ElectricalResistance = Microohms(n)
    def milliohms: ElectricalResistance = Milliohms(n)
    def kilohms: ElectricalResistance = Kilohms(n)
    def megohms: ElectricalResistance = Megohms(n)
    def gigohms: ElectricalResistance = Gigohms(n)
  }

  given ElectricalResistanceNumeric: AbstractQuantityNumeric[ElectricalResistance](ElectricalResistance.primaryUnit) {}
}
