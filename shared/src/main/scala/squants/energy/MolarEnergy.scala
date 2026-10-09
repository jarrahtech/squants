package squants.energy

import squants.mass.{ ChemicalAmount, Moles }
import squants.{ AbstractQuantityNumeric, Dimension, PrimaryUnit, Quantity, SiUnit, UnitConverter, UnitOfMeasure }
import scala.util.Try

/**
 *
 * @author Nicolas Vinuesa
 * @since 1.4
 *
 * @param value Double
 */
final class MolarEnergy private (val value: Double, val unit: MolarEnergyUnit)
  extends Quantity[MolarEnergy] {

  def dimension = MolarEnergy

  def *(that: ChemicalAmount): Energy = Joules(this.toJoulesPerMole * that.toMoles)

  def toJoulesPerMole: Double = to(JoulesPerMole)
}

object MolarEnergy extends Dimension[MolarEnergy] {
  private[energy] def apply[A](n: A, unit: MolarEnergyUnit)(using num: Numeric[A]) = new MolarEnergy(num.toDouble(n), unit)
  def apply(value: Any): Try[MolarEnergy] = parse(value)
  def name = "MolarEnergy"
  def primaryUnit = JoulesPerMole
  def siUnit = JoulesPerMole
  def units: Set[UnitOfMeasure[MolarEnergy]] = Set(JoulesPerMole)
}

trait MolarEnergyUnit extends UnitOfMeasure[MolarEnergy] with UnitConverter {
  def apply[A](n: A)(using num: Numeric[A]): MolarEnergy = MolarEnergy(n, this)
}

object JoulesPerMole extends MolarEnergyUnit with PrimaryUnit with SiUnit {
  val symbol: String = Joules.symbol + "/" + Moles.symbol
}

object MolarEnergyConversions {
  lazy val joulePerMole: MolarEnergy = JoulesPerMole(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def joulesPerMole: MolarEnergy = JoulesPerMole(n)
  }

  given MolarEnergyNumeric: AbstractQuantityNumeric[MolarEnergy](MolarEnergy.primaryUnit) {}
}
