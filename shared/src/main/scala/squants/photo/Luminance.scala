/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2013-2015, Gary Keorkunian                                       **
**                                                                      **
\*                                                                      */
package squants.photo

import squants._
import scala.util.Try

/**
 * @author  garyKeorkunian
 * @since   0.1
 *
 * @param value Double
 */
final class Luminance private (val value: Double, val unit: LuminanceUnit)
  extends Quantity[Luminance] {

  def dimension = Luminance

  def *(that: Area): LuminousIntensity = Candelas(this.value * that.toSquareMeters)

  def toCandelasPerSquareMeters: Double = to(CandelasPerSquareMeter)
}

object Luminance extends Dimension[Luminance] {
  private[photo] def apply[A](n: A, unit: LuminanceUnit)(using num: Numeric[A]) = new Luminance(num.toDouble(n), unit)
  def apply(value: Any): Try[Luminance] = parse(value)

  def name = "Luminance"
  def primaryUnit = CandelasPerSquareMeter
  def siUnit = CandelasPerSquareMeter
  def units: Set[UnitOfMeasure[Luminance]] = Set(CandelasPerSquareMeter)
}

trait LuminanceUnit extends UnitOfMeasure[Luminance] {
  def apply[A](n: A)(using num: Numeric[A]): Luminance = Luminance(num.toDouble(n), this)
}

object CandelasPerSquareMeter extends LuminanceUnit with PrimaryUnit with SiUnit {
  val symbol = "cd/m²"
}

object LuminanceConversions {
  lazy val candelaPerSquareMeter: Luminance = CandelasPerSquareMeter(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def candelasPerSquareMeter: Luminance = CandelasPerSquareMeter(n)
  }

  given LuminanceNumeric: AbstractQuantityNumeric[Luminance](Luminance.primaryUnit) {}
}

