package squants.electro

import squants.mass.Kilograms
import squants.{ AbstractQuantityNumeric, Dimension, Mass, PrimaryUnit, Quantity, SiUnit, UnitConverter, UnitOfMeasure }
import scala.util.Try

/**
 *
 * @author Nicolas Vinuesa
 * @since 1.4
 *
 * @param value Double
 */
final class ElectricChargeMassRatio private (val value: Double, val unit: ElectricChargeMassRatioUnit)
  extends Quantity[ElectricChargeMassRatio] {

  def dimension = ElectricChargeMassRatio

  def *(that: Mass): ElectricCharge = Coulombs(this.toCoulombsKilograms * that.toKilograms)

  def toCoulombsKilograms: Double = to(CoulombsPerKilogram)
}

object ElectricChargeMassRatio extends Dimension[ElectricChargeMassRatio] {
  private[electro] def apply[A](n: A, unit: ElectricChargeMassRatioUnit)(using num: Numeric[A]) = new ElectricChargeMassRatio(num.toDouble(n), unit)
  def apply(value: Any): Try[ElectricChargeMassRatio] = parse(value)
  def name = "ElectricChargeMassRatio"
  def primaryUnit = CoulombsPerKilogram
  def siUnit = CoulombsPerKilogram
  def units: Set[UnitOfMeasure[ElectricChargeMassRatio]] = Set(CoulombsPerKilogram)
}

trait ElectricChargeMassRatioUnit extends UnitOfMeasure[ElectricChargeMassRatio] with UnitConverter {
  def apply[A](n: A)(using num: Numeric[A]): ElectricChargeMassRatio = ElectricChargeMassRatio(n, this)
}

object CoulombsPerKilogram extends ElectricChargeMassRatioUnit with PrimaryUnit with SiUnit {
  val symbol: String = Coulombs.symbol + "/" + Kilograms.symbol
}

object ElectricChargeMassRatioConversions {
  lazy val coulombPerKilogram: ElectricChargeMassRatio = CoulombsPerKilogram(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def coulombsPerKilogram: ElectricChargeMassRatio = CoulombsPerKilogram(n)
  }

  given ElectricChargeMassRatioNumeric: AbstractQuantityNumeric[ElectricChargeMassRatio](ElectricChargeMassRatio.primaryUnit) {}
}