/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2013-2015, Gary Keorkunian                                       **
**                                                                      **
\*                                                                      */

package squants.energy

import squants._
import squants.space.CubicMeters
import scala.util.Try

/**
 * Represents a quantity of energy
 *
 * @author  garyKeorkunian
 * @since   0.1
 *
 * @param value value in [[squants.energy.WattHours]]
 */
final class EnergyDensity private (val value: Double, val unit: EnergyDensityUnit)
  extends Quantity[EnergyDensity] {

  def dimension = EnergyDensity

  def *(that: Volume): Energy = Joules(this.toJoulesPerCubicMeter * that.toCubicMeters)

  def toJoulesPerCubicMeter: Double = to(JoulesPerCubicMeter)
}

object EnergyDensity extends Dimension[EnergyDensity] {
  private[energy] def apply[A](n: A, unit: EnergyDensityUnit)(using num: Numeric[A]) = new EnergyDensity(num.toDouble(n), unit)
  def apply(value: Any): Try[EnergyDensity] = parse(value)
  def name = "EnergyDensity"
  def primaryUnit = JoulesPerCubicMeter
  def siUnit = JoulesPerCubicMeter
  def units: Set[UnitOfMeasure[EnergyDensity]] = Set(JoulesPerCubicMeter)
}

trait EnergyDensityUnit extends UnitOfMeasure[EnergyDensity] with UnitConverter {
  def apply[A](n: A)(using num: Numeric[A]): EnergyDensity = EnergyDensity(n, this)
}

object JoulesPerCubicMeter extends EnergyDensityUnit with PrimaryUnit with SiUnit {
  val symbol: String = Joules.symbol + "/" + CubicMeters.symbol
}

object EnergyDensityConversions {
  lazy val joulePerCubicMeter: EnergyDensity = JoulesPerCubicMeter(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def joulesPerCubicMeter: EnergyDensity = JoulesPerCubicMeter(n)
  }

  given EnergyDensityNumeric: AbstractQuantityNumeric[EnergyDensity](EnergyDensity.primaryUnit) {}
}
