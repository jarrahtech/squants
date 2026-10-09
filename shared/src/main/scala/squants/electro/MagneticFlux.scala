/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2013-2015, Gary Keorkunian                                       **
**                                                                      **
\*                                                                      */

package squants.electro

import squants._
import squants.space.SquareMeters
import squants.time.TimeIntegral
import scala.util.Try

/**
 * @author  garyKeorkunian
 * @since   0.1
 *
 * @param value value in [[squants.electro.Webers]]
 */
final class MagneticFlux private (val value: Double, val unit: MagneticFluxUnit)
  extends Quantity[MagneticFlux]
  with TimeIntegral[ElectricPotential] {

  def dimension = MagneticFlux

  protected def timeDerived: ElectricPotential = Volts(toWebers)
  protected def time: Time = Seconds(1)

  def /(that: Area): MagneticFluxDensity = Teslas(this.toWebers / that.toSquareMeters)
  def /(that: MagneticFluxDensity): Area = SquareMeters(this.toWebers / that.toTeslas)
  def /(that: ElectricCurrent): Inductance = Henry(this.toWebers / that.toAmperes)
  def /(that: Inductance): ElectricCurrent = Amperes(this.toWebers / that.toHenry)

  def toWebers: Double = to(Webers)
}

object MagneticFlux extends Dimension[MagneticFlux] {
  private[electro] def apply[A](n: A, unit: MagneticFluxUnit)(using num: Numeric[A]) = new MagneticFlux(num.toDouble(n), unit)
  def apply(value: Any): Try[MagneticFlux] = parse(value)
  def name = "MagneticFlux"
  def primaryUnit = Webers
  def siUnit = Webers
  def units: Set[UnitOfMeasure[MagneticFlux]] = Set(Webers)
}

trait MagneticFluxUnit extends UnitOfMeasure[MagneticFlux] with UnitConverter {
  def apply[A](n: A)(using num: Numeric[A]): MagneticFlux = MagneticFlux(n, this)
}

object Webers extends MagneticFluxUnit with PrimaryUnit with SiUnit {
  val symbol = "Wb"
}

object MagneticFluxConversions {
  lazy val weber: MagneticFlux = Webers(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def webers: MagneticFlux = Webers(n)
  }

  given MagneticFluxNumeric: AbstractQuantityNumeric[MagneticFlux](MagneticFlux.primaryUnit) {}
}