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
final class LinearElectricChargeDensity private (val value: Double, val unit: LinearElectricChargeDensityUnit)
  extends Quantity[LinearElectricChargeDensity] {

  def dimension = LinearElectricChargeDensity

  def *(that: Length): ElectricCharge = Coulombs(this.toCoulombsMeters * that.toMeters)
  def /(that: Length): AreaElectricChargeDensity = CoulombsPerSquareMeter(this.toCoulombsMeters / that.toMeters)

  def toCoulombsMeters: Double = to(CoulombsPerMeter)
}

object LinearElectricChargeDensity extends Dimension[LinearElectricChargeDensity] {
  private[electro] def apply[A](n: A, unit: LinearElectricChargeDensityUnit)(using num: Numeric[A]) = new LinearElectricChargeDensity(num.toDouble(n), unit)
  def apply(value: Any): Try[LinearElectricChargeDensity] = parse(value)
  def name = "LinearElectricChargeDensity"
  def primaryUnit = CoulombsPerMeter
  def siUnit = CoulombsPerMeter
  def units: Set[UnitOfMeasure[LinearElectricChargeDensity]] = Set(CoulombsPerMeter)
}

trait LinearElectricChargeDensityUnit extends UnitOfMeasure[LinearElectricChargeDensity] with UnitConverter {
  def apply[A](n: A)(using num: Numeric[A]): LinearElectricChargeDensity = LinearElectricChargeDensity(n, this)
}

object CoulombsPerMeter extends LinearElectricChargeDensityUnit with PrimaryUnit with SiUnit {
  val symbol: String = Coulombs.symbol + "/" + Meters.symbol
}

object LinearElectricChargeDensityConversions {
  lazy val coulombPerMeter: LinearElectricChargeDensity = CoulombsPerMeter(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def coulombsPerMeter: LinearElectricChargeDensity = CoulombsPerMeter(n)
  }

  given LinearElectricChargeDensityNumeric: AbstractQuantityNumeric[LinearElectricChargeDensity](LinearElectricChargeDensity.primaryUnit) {}
}