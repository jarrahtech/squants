package squants.electro

import squants.space.Meters
import squants.{ AbstractQuantityNumeric, Dimension, Length, PrimaryUnit, Quantity, SiUnit, UnitConverter, UnitOfMeasure }
import scala.util.Try

/**
 *
 * @author Nicolas Vinuesa
 * @since 1.4
 *
 * @param value Double
 */
final class MagneticFieldStrength private (val value: Double, val unit: MagneticFieldStrengthUnit)
  extends Quantity[MagneticFieldStrength] {

  def dimension = MagneticFieldStrength

  def *(that: Length): ElectricCurrent = Amperes(this.toAmperesPerMeter * that.toMeters)

  def toAmperesPerMeter: Double = to(AmperesPerMeter)
}

object MagneticFieldStrength extends Dimension[MagneticFieldStrength] {
  private[electro] def apply[A](n: A, unit: MagneticFieldStrengthUnit)(using num: Numeric[A]) = new MagneticFieldStrength(num.toDouble(n), unit)
  def apply(value: Any): Try[MagneticFieldStrength] = parse(value)
  def name = "MagneticFieldStrength"
  def primaryUnit = AmperesPerMeter
  def siUnit = AmperesPerMeter
  def units: Set[UnitOfMeasure[MagneticFieldStrength]] = Set(AmperesPerMeter)
}

trait MagneticFieldStrengthUnit extends UnitOfMeasure[MagneticFieldStrength] with UnitConverter {
  def apply[A](n: A)(using num: Numeric[A]): MagneticFieldStrength = MagneticFieldStrength(n, this)
}

object AmperesPerMeter extends MagneticFieldStrengthUnit with PrimaryUnit with SiUnit {
  val symbol: String = Amperes.symbol + "/" + Meters.symbol
}

object MagneticFieldStrengthConversions {
  lazy val amperePerMeter: MagneticFieldStrength = AmperesPerMeter(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def amperesPerMeter: MagneticFieldStrength = AmperesPerMeter(n)
  }

  given MagneticFieldStrengthNumeric: AbstractQuantityNumeric[MagneticFieldStrength](MagneticFieldStrength.primaryUnit) {}
}
