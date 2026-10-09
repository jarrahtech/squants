package squants.electro

import squants.space.Meters
import squants.{ AbstractQuantityNumeric, Area, Dimension, Length, PrimaryUnit, Quantity, SiUnit, UnitConverter, UnitOfMeasure }
import scala.util.Try

/**
 *
 * @author Nicolas Vinuesa
 * @since 1.4
 *
 * @param value Double
 */
final class ElectricCurrentDensity private (val value: Double, val unit: ElectricCurrentDensityUnit)
  extends Quantity[ElectricCurrentDensity] {

  def dimension = ElectricCurrentDensity

  def *(that: Area): ElectricCurrent = Amperes(this.toAmperesPerSquareMeter * that.toSquareMeters)
  def *(that: Length): MagneticFieldStrength = AmperesPerMeter(this.toAmperesPerSquareMeter * that.toMeters)

  def toAmperesPerSquareMeter: Double = to(AmperesPerSquareMeter)
}

object ElectricCurrentDensity extends Dimension[ElectricCurrentDensity] {
  private[electro] def apply[A](n: A, unit: ElectricCurrentDensityUnit)(using num: Numeric[A]) = new ElectricCurrentDensity(num.toDouble(n), unit)
  def apply(value: Any): Try[ElectricCurrentDensity] = parse(value)
  def name = "ElectricCurrentDensity"
  def primaryUnit = AmperesPerSquareMeter
  def siUnit = AmperesPerSquareMeter
  def units: Set[UnitOfMeasure[ElectricCurrentDensity]] = Set(AmperesPerSquareMeter)
}

trait ElectricCurrentDensityUnit extends UnitOfMeasure[ElectricCurrentDensity] with UnitConverter {
  def apply[A](n: A)(using num: Numeric[A]): ElectricCurrentDensity = ElectricCurrentDensity(n, this)
}

object AmperesPerSquareMeter extends ElectricCurrentDensityUnit with PrimaryUnit with SiUnit {
  val symbol: String = Amperes.symbol + "/" + Meters.symbol + "²"
}

object ElectricCurrentDensityConversions {
  lazy val amperePerSquareMeter: ElectricCurrentDensity = AmperesPerSquareMeter(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def amperesPerSquareMeter: ElectricCurrentDensity = AmperesPerSquareMeter(n)
  }

  given ElectricCurrentDensityNumeric: AbstractQuantityNumeric[ElectricCurrentDensity](ElectricCurrentDensity.primaryUnit) {}
}
