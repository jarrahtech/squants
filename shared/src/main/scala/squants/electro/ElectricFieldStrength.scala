package squants.electro

import squants.space.{ Length, Meters }
import squants.{ AbstractQuantityNumeric, Dimension, PrimaryUnit, Quantity, SiUnit, UnitConverter, UnitOfMeasure }
import scala.util.Try

/**
 *
 * @author Nicolas Vinuesa
 * @since 1.4
 *
 * @param value Double
 */
final class ElectricFieldStrength private (val value: Double, val unit: ElectricFieldStrengthUnit)
  extends Quantity[ElectricFieldStrength] {

  def dimension = ElectricFieldStrength

  def *(that: Length): ElectricPotential = Volts(this.toVoltsPerMeter * that.toMeters)

  def toVoltsPerMeter: Double = to(VoltsPerMeter)
}

object ElectricFieldStrength extends Dimension[ElectricFieldStrength] {
  private[electro] def apply[A](n: A, unit: ElectricFieldStrengthUnit)(using num: Numeric[A]) = new ElectricFieldStrength(num.toDouble(n), unit)
  def apply(value: Any): Try[ElectricFieldStrength] = parse(value)
  def name = "ElectricFieldStrength"
  def primaryUnit = VoltsPerMeter
  def siUnit = VoltsPerMeter
  def units: Set[UnitOfMeasure[ElectricFieldStrength]] = Set(VoltsPerMeter)
}

trait ElectricFieldStrengthUnit extends UnitOfMeasure[ElectricFieldStrength] with UnitConverter {
  def apply[A](n: A)(using num: Numeric[A]): ElectricFieldStrength = ElectricFieldStrength(n, this)
}

object VoltsPerMeter extends ElectricFieldStrengthUnit with PrimaryUnit with SiUnit {
  val symbol: String = Volts.symbol + "/" + Meters.symbol
}

object ElectricFieldStrengthConversions {
  lazy val voltPerMeter: ElectricFieldStrength = VoltsPerMeter(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def voltsPerMeter: ElectricFieldStrength = VoltsPerMeter(n)
  }

  given ElectricFieldStrengthNumeric: AbstractQuantityNumeric[ElectricFieldStrength](ElectricFieldStrength.primaryUnit) {}
}
