/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2013-2015, Gary Keorkunian                                       **
**                                                                      **
\*                                                                      */

package squants.electro

import squants._
import scala.util.Try

/**
 * @author  garyKeorkunian
 * @since   0.1
 *
 * @param value value in [[squants.electro.Teslas]]
 */
final class MagneticFluxDensity private (val value: Double, val unit: MagneticFluxDensityUnit)
  extends Quantity[MagneticFluxDensity] {

  def dimension = MagneticFluxDensity

  def *(that: Area): MagneticFlux = Webers(this.toTeslas * that.toSquareMeters)

  def toTeslas: Double = to(Teslas)
  def toGuass: Double = to(Gauss)
}

object MagneticFluxDensity extends Dimension[MagneticFluxDensity] {
  private[electro] def apply[A](n: A, unit: MagneticFluxDensityUnit)(using num: Numeric[A]) = new MagneticFluxDensity(num.toDouble(n), unit)
  def apply(value: Any): Try[MagneticFluxDensity] = parse(value)
  def name = "MagneticFluxDensity"
  def primaryUnit = Teslas
  def siUnit = Teslas
  def units: Set[UnitOfMeasure[MagneticFluxDensity]] = Set(Teslas, Gauss)
}

trait MagneticFluxDensityUnit extends UnitOfMeasure[MagneticFluxDensity] with UnitConverter {
  def apply[A](n: A)(using num: Numeric[A]): MagneticFluxDensity = MagneticFluxDensity(n, this)
}

object Teslas extends MagneticFluxDensityUnit with PrimaryUnit with SiUnit {
  val symbol = "T"
}

object Gauss extends MagneticFluxDensityUnit {
  val conversionFactor: Double = 100 * MetricSystem.Micro
  val symbol = "Gs"
}

object MagneticFluxDensityConversions {
  lazy val tesla: MagneticFluxDensity = Teslas(1)
  lazy val gauss: MagneticFluxDensity = Gauss(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def teslas: MagneticFluxDensity = Teslas(n)
    def gauss: MagneticFluxDensity = Gauss(n)
  }

  given MagneticFluxDensistyNumeric: AbstractQuantityNumeric[MagneticFluxDensity](MagneticFluxDensity.primaryUnit) {}
}
