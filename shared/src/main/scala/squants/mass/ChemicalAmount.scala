/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2013-2015, Gary Keorkunian                                       **
**                                                                      **
\*                                                                      */

package squants.mass

import squants._
import scala.util.Try

/**
 * @author  garyKeorkunian
 * @since   0.1
 *
 * @param value in [[squants.mass.Moles]]
 */
final class ChemicalAmount private (val value: Double, val unit: ChemicalAmountUnit)
  extends Quantity[ChemicalAmount] {

  def dimension = ChemicalAmount

  def /(that: Volume) = ??? // returns SubstanceConcentration

  def toMoles: Double = to(Moles)
  def toPoundMoles: Double = to(PoundMoles)
}

object ChemicalAmount extends Dimension[ChemicalAmount] with BaseDimension {
  private[mass] def apply[A](n: A, unit: ChemicalAmountUnit)(using num: Numeric[A]) = new ChemicalAmount(num.toDouble(n), unit)
  def apply(value: Any): Try[ChemicalAmount] = parse(value)
  val name = "ChemicalAmount"
  def primaryUnit = Moles
  def siUnit = Moles
  def units: Set[UnitOfMeasure[ChemicalAmount]] = Set(Moles, PoundMoles)
  def dimensionSymbol = "N"
}

trait ChemicalAmountUnit extends UnitOfMeasure[ChemicalAmount] with UnitConverter {
  def apply[A](n: A)(using num: Numeric[A]): ChemicalAmount = ChemicalAmount(n, this)
}

object Moles extends ChemicalAmountUnit with PrimaryUnit with SiBaseUnit {
  val symbol = "mol"
}

object PoundMoles extends ChemicalAmountUnit {
  val symbol = "lb-mol"
  val conversionFactor = 453.59237
}

object ChemicalAmountConversions {
  lazy val mole: ChemicalAmount = Moles(1)
  lazy val poundMole: ChemicalAmount = PoundMoles(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def moles: ChemicalAmount = Moles(n)
    def poundMoles: ChemicalAmount = PoundMoles(n)
  }

  given ChemicalAmountNumeric: AbstractQuantityNumeric[ChemicalAmount](ChemicalAmount.primaryUnit) {}
}