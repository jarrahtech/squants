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
final class AreaElectricChargeDensity private (val value: Double, val unit: AreaElectricChargeDensityUnit)
  extends Quantity[AreaElectricChargeDensity] {

  def dimension = AreaElectricChargeDensity

  def *(that: Area): ElectricCharge = Coulombs(this.toCoulombsSquareMeters * that.toSquareMeters)
  def *(that: Length): LinearElectricChargeDensity = CoulombsPerMeter(this.toCoulombsSquareMeters * that.toMeters)

  def toCoulombsSquareMeters: Double = to(CoulombsPerSquareMeter)
}

object AreaElectricChargeDensity extends Dimension[AreaElectricChargeDensity] {
  private[electro] def apply[A](n: A, unit: AreaElectricChargeDensityUnit)(using num: Numeric[A]) = new AreaElectricChargeDensity(num.toDouble(n), unit)
  def apply(value: Any): Try[AreaElectricChargeDensity] = parse(value)
  def name = "AreaElectricChargeDensity"
  def primaryUnit = CoulombsPerSquareMeter
  def siUnit = CoulombsPerSquareMeter
  def units: Set[UnitOfMeasure[AreaElectricChargeDensity]] = Set(CoulombsPerSquareMeter)
}

trait AreaElectricChargeDensityUnit extends UnitOfMeasure[AreaElectricChargeDensity] with UnitConverter {
  def apply[A](n: A)(using num: Numeric[A]): AreaElectricChargeDensity = AreaElectricChargeDensity(n, this)
}

object CoulombsPerSquareMeter extends AreaElectricChargeDensityUnit with PrimaryUnit with SiUnit {
  val symbol: String = Coulombs.symbol + "/" + Meters.symbol + "²"
}

object AreaElectricChargeDensityConversions {
  lazy val coulombPerSquareMeter: AreaElectricChargeDensity = CoulombsPerSquareMeter(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def coulombsPerSquareMeter: AreaElectricChargeDensity = CoulombsPerSquareMeter(n)
  }

  given AreaElectricChargeDensityNumeric: AbstractQuantityNumeric[AreaElectricChargeDensity](AreaElectricChargeDensity.primaryUnit) {}
}