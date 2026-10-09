package squants.electro

import squants.space.{ Meters, Volume }
import squants.{ AbstractQuantityNumeric, Area, Dimension, Length, PrimaryUnit, Quantity, SiUnit, UnitConverter, UnitOfMeasure }
import scala.util.Try

/**
 *
 * @author Nicolas Vinuesa
 * @since 1.4
 *
 * @param value Double
 */
final class ElectricChargeDensity private (val value: Double, val unit: ElectricChargeDensityUnit)
  extends Quantity[ElectricChargeDensity] {

  def dimension = ElectricChargeDensity

  def *(that: Volume): ElectricCharge = Coulombs(this.toCoulombsCubicMeters * that.toCubicMeters)
  def *(that: Area): LinearElectricChargeDensity = CoulombsPerMeter(this.toCoulombsCubicMeters * that.toSquareMeters)
  def *(that: Length): AreaElectricChargeDensity = CoulombsPerSquareMeter(this.toCoulombsCubicMeters * that.toMeters)

  def toCoulombsCubicMeters: Double = to(CoulombsPerCubicMeter)
}

object ElectricChargeDensity extends Dimension[ElectricChargeDensity] {
  private[electro] def apply[A](n: A, unit: ElectricChargeDensityUnit)(using num: Numeric[A]) = new ElectricChargeDensity(num.toDouble(n), unit)
  def apply(value: Any): Try[ElectricChargeDensity] = parse(value)
  def name = "ElectricChargeDensity"
  def primaryUnit = CoulombsPerCubicMeter
  def siUnit = CoulombsPerCubicMeter
  def units: Set[UnitOfMeasure[ElectricChargeDensity]] = Set(CoulombsPerCubicMeter)
}

trait ElectricChargeDensityUnit extends UnitOfMeasure[ElectricChargeDensity] with UnitConverter {
  def apply[A](n: A)(using num: Numeric[A]): ElectricChargeDensity = ElectricChargeDensity(n, this)
}

object CoulombsPerCubicMeter extends ElectricChargeDensityUnit with PrimaryUnit with SiUnit {
  val symbol: String = Coulombs.symbol + "/" + Meters.symbol + "³"
}

object ElectricChargeDensityConversions {
  lazy val coulombPerCubicMeter: ElectricChargeDensity = CoulombsPerCubicMeter(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def coulombsPerCubicMeter: ElectricChargeDensity = CoulombsPerCubicMeter(n)
  }

  given ElectricChargeDensityNumeric: AbstractQuantityNumeric[ElectricChargeDensity](ElectricChargeDensity.primaryUnit) {}
}