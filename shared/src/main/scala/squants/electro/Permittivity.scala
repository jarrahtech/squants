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
final class Permittivity private (val value: Double, val unit: PermittivityUnit)
  extends Quantity[Permittivity] {

  def dimension = Permittivity

  def *(that: Length): Capacitance = Farads(this.toFaradsMeters * that.toMeters)

  def toFaradsMeters: Double = to(FaradsPerMeter)
}

object Permittivity extends Dimension[Permittivity] {
  private[electro] def apply[A](n: A, unit: PermittivityUnit)(using num: Numeric[A]) = new Permittivity(num.toDouble(n), unit)
  def apply(value: Any): Try[Permittivity] = parse(value)
  def name = "Permittivity"
  def primaryUnit = FaradsPerMeter
  def siUnit = FaradsPerMeter
  def units: Set[UnitOfMeasure[Permittivity]] = Set(FaradsPerMeter)
}

trait PermittivityUnit extends UnitOfMeasure[Permittivity] with UnitConverter {
  def apply[A](n: A)(using num: Numeric[A]): Permittivity = Permittivity(n, this)
}

object FaradsPerMeter extends PermittivityUnit with PrimaryUnit with SiUnit {
  val symbol: String = Farads.symbol + "/" + Meters.symbol
}

object PermittivityConversions {
  lazy val faradPerMeter: Permittivity = FaradsPerMeter(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def faradsPerMeter: Permittivity = FaradsPerMeter(n)
  }

  given PermittivityNumeric: AbstractQuantityNumeric[Permittivity](Permittivity.primaryUnit) {}
}
