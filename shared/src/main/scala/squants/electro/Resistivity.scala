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
 * @param value value in [[squants.electro.OhmMeters]]
 */
final class Resistivity private (val value: Double, val unit: ResistivityUnit)
  extends Quantity[Resistivity] {

  def dimension = Resistivity

  def /(that: Length): ElectricalResistance = Ohms(this.toOhmMeters / that.toMeters)
  def /(that: ElectricalResistance): Length = Meters(this.toOhmMeters / that.toOhms)

  def toOhmMeters: Double = to(OhmMeters)
  def inSiemensPerMeter: Conductivity = SiemensPerMeter(1d / toOhmMeters)
}

object Resistivity extends Dimension[Resistivity] {
  private[electro] def apply[A](n: A, unit: ResistivityUnit)(using num: Numeric[A]) = new Resistivity(num.toDouble(n), unit)
  def apply(value: Any): Try[Resistivity] = parse(value)
  def name = "Resistivity"
  def primaryUnit = OhmMeters
  def siUnit = OhmMeters
  def units: Set[UnitOfMeasure[Resistivity]] = Set(OhmMeters)
}

trait ResistivityUnit extends UnitOfMeasure[Resistivity] with UnitConverter {
  def apply[A](n: A)(using num: Numeric[A]): Resistivity = Resistivity(n, this)
}

object OhmMeters extends ResistivityUnit with PrimaryUnit with SiUnit {
  def symbol = "Ω⋅m"
}

object ResistivityConversions {
  lazy val ohmMeter: Resistivity = OhmMeters(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def ohmMeters: Resistivity = OhmMeters(n)
  }

  given ResistivityNumeric: AbstractQuantityNumeric[Resistivity](Resistivity.primaryUnit) {}
}
