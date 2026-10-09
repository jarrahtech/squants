/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2013-2015, Gary Keorkunian                                       **
**                                                                      **
\*                                                                      */

package squants.motion

import squants._
import squants.time.{ Seconds, TimeDerivative }
import scala.util.Try

/**
 * @author  stevebarham
 * @since   0.5.2
 *
 * @param value Double
 */
final class PressureChange private (val value: Double, val unit: PressureChangeUnit)
  extends Quantity[PressureChange]
  with TimeDerivative[Pressure] {

  def dimension = PressureChange

  protected[squants] def timeIntegrated: Pressure = Pascals(toPascalsPerSecond)
  protected[squants] def time: Time = Seconds(1)

  def toPascalsPerSecond: Double = to(PascalsPerSecond)
  def toBarsPerSecond: Double = to(BarsPerSecond)
  def toPoundsPerSquareInchPerSecond: Double = to(PoundsPerSquareInchPerSecond)
  def toStandardAtmospheresPerSecond: Double = to(StandardAtmospheresPerSecond)
}

object PressureChange extends Dimension[PressureChange] {
  private[motion] def apply[A](n: A, unit: PressureChangeUnit)(using num: Numeric[A]) = new PressureChange(num.toDouble(n), unit)
  def apply(value: Any): Try[PressureChange] = parse(value)
  def name = "PressureChange"
  def primaryUnit = PascalsPerSecond
  def siUnit = PascalsPerSecond
  def units: Set[UnitOfMeasure[PressureChange]] = Set(PascalsPerSecond, BarsPerSecond, PoundsPerSquareInchPerSecond, StandardAtmospheresPerSecond)
}

trait PressureChangeUnit extends UnitOfMeasure[PressureChange] with UnitConverter {
  def apply[A](n: A)(using num: Numeric[A]): PressureChange = PressureChange(n, this)
}

object PascalsPerSecond extends PressureChangeUnit with PrimaryUnit with SiUnit {
  val symbol = "Pa/s"
}

object BarsPerSecond extends PressureChangeUnit {
  val symbol = "bar/s"
  val conversionFactor: Double = Bars.conversionFactor / Pascals.conversionFactor
}

object PoundsPerSquareInchPerSecond extends PressureChangeUnit {
  val symbol = "psi/s"
  val conversionFactor = PoundsPerSquareInch.conversionFactor
}

object StandardAtmospheresPerSecond extends PressureChangeUnit {
  val symbol = "atm/s"
  val conversionFactor = StandardAtmospheres.conversionFactor
}

object PressureChangeConversions {
  lazy val pascalsPerSecond: PressureChange = PascalsPerSecond(1)
  lazy val barsPerSecond: PressureChange = BarsPerSecond(1)
  lazy val poundsPerSquareInchPerSecond: PressureChange = PoundsPerSquareInchPerSecond(1)
  lazy val standardAtmospheresPerSecond: PressureChange = StandardAtmospheresPerSecond(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def pascalsPerSecond: PressureChange = PascalsPerSecond(n)
    def barsPerSecond: PressureChange = BarsPerSecond(n)
    def poundsPerSquareInchPerSecond: PressureChange = PoundsPerSquareInchPerSecond(n)
    def standardAtmospheresPerSecond: PressureChange = StandardAtmospheresPerSecond(n)
  }

  given PressureChangeNumeric: AbstractQuantityNumeric[PressureChange](PressureChange.primaryUnit) {}
}
