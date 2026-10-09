/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2013-2015, Gary Keorkunian                                       **
**                                                                      **
\*                                                                      */

package squants.electro

import squants._
import squants.space.Length
import scala.util.Try

/**
 * @author  garyKeorkunian
 * @since   0.1
 *
 * @param value value in [[squants.electro.SiemensPerMeter]]
 */
final class Conductivity private (val value: Double, val unit: ConductivityUnit)
  extends Quantity[Conductivity] {

  def dimension = Conductivity

  def *(that: Length): ElectricalConductance = Siemens(this.toSiemensPerMeter * that.toMeters)

  def toSiemensPerMeter: Double = to(SiemensPerMeter)
  def inOhmMeters: Resistivity = OhmMeters(1d / toSiemensPerMeter)
}

object Conductivity extends Dimension[Conductivity] {
  private[electro] def apply[A](n: A, unit: ConductivityUnit)(using num: Numeric[A]) = new Conductivity(num.toDouble(n), unit)
  def apply(value: Any): Try[Conductivity] = parse(value)
  def name = "Conductivity"
  def primaryUnit = SiemensPerMeter
  def siUnit = SiemensPerMeter
  def units: Set[UnitOfMeasure[Conductivity]] = Set(SiemensPerMeter)
}

trait ConductivityUnit extends UnitOfMeasure[Conductivity] with UnitConverter {
  def apply[A](n: A)(using num: Numeric[A]): Conductivity = Conductivity(n, this)
}

object SiemensPerMeter extends ConductivityUnit with PrimaryUnit with SiUnit {
  val symbol = "S/m"
}

object ConductivityConversions {
  lazy val siemenPerMeter: Conductivity = SiemensPerMeter(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def siemensPerMeter: Conductivity = SiemensPerMeter(n)
  }

  given ConductivityNumeric: AbstractQuantityNumeric[Conductivity](Conductivity.primaryUnit) {}
}
