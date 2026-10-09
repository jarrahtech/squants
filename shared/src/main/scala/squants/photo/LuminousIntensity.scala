/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2013-2015, Gary Keorkunian                                       **
**                                                                      **
\*                                                                      */
package squants.photo

import squants._
import squants.space.{ SolidAngle, SquareMeters }
import scala.util.Try

/**
 * @author  garyKeorkunian
 * @since   0.1
 *
 * @param value value in [[squants.photo.Candelas]]
 */
final class LuminousIntensity private (val value: Double, val unit: LuminousIntensityUnit)
  extends Quantity[LuminousIntensity] {

  def dimension = LuminousIntensity

  def *(that: SolidAngle): LuminousFlux = Lumens(this.toCandelas * that.toSquaredRadians)
  def /(that: Area): Luminance = CandelasPerSquareMeter(this.toCandelas / that.toSquareMeters)
  def /(that: Luminance): Area = SquareMeters(this.toCandelas / that.toCandelasPerSquareMeters)

  def toCandelas: Double = to(Candelas)
}

object LuminousIntensity extends Dimension[LuminousIntensity] with BaseDimension {
  private[photo] def apply[A](n: A, unit: LuminousIntensityUnit)(using num: Numeric[A]) = new LuminousIntensity(num.toDouble(n), unit)
  def apply(value: Any): Try[LuminousIntensity] = parse(value)
  def name = "LuminousIntensity"
  def primaryUnit = Candelas
  def units: Set[UnitOfMeasure[LuminousIntensity]] = Set(Candelas)
  def siUnit = Candelas
  def dimensionSymbol = "J"
}

trait LuminousIntensityUnit extends UnitOfMeasure[LuminousIntensity] with UnitConverter {
  def apply[A](n: A)(using num: Numeric[A]): LuminousIntensity = LuminousIntensity(n, this)
}

object Candelas extends LuminousIntensityUnit with PrimaryUnit with SiBaseUnit {
  val symbol = "cd"
}

object LuminousIntensityConversions {
  lazy val candela: LuminousIntensity = Candelas(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def candelas: LuminousIntensity = Candelas(n)
  }

  given LuminousIntensityNumeric: AbstractQuantityNumeric[LuminousIntensity](LuminousIntensity.primaryUnit) {}
}