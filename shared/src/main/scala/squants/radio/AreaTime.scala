/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2013-2015, Gary Keorkunian                                       **
**                                                                      **
\*                                                                      */

package squants.radio

import squants._
import squants.space.SquareMeters
import scala.util.Try

/**
 * @author  Hunter Payne
 *
 * @param value Double
 */
final class AreaTime private (val value: Double, val unit: AreaTimeUnit)
  extends Quantity[AreaTime] {

  def dimension = AreaTime

  def /(that: Area): Time =
    Seconds(this.toSquareMeterSeconds / that.toSquareMeters)
  def /(that: Time): Area =
    SquareMeters(this.toSquareMeterSeconds / that.toSeconds)

  def toSquareMeterSeconds: Double = to(SquareMeterSeconds)
  def toSquareCentimeterSeconds: Double = to(SquareCentimeterSeconds)
}

/**
 * Factory singleton for [[squants.radio.AreaTime]] values
 */
object AreaTime extends Dimension[AreaTime] {
  private[radio] def apply[A](n: A, unit: AreaTimeUnit)(using num: Numeric[A]) = new AreaTime(num.toDouble(n), unit)
  def apply(area: Area, time: Time): AreaTime =
    SquareMeterSeconds(area.toSquareMeters * time.toSeconds)
  def apply(value: Any): Try[AreaTime] = parse(value)
  def name = "AreaTime"
  def primaryUnit = SquareMeterSeconds
  def siUnit = SquareMeterSeconds
  def units: Set[UnitOfMeasure[AreaTime]] = Set(SquareMeterSeconds, SquareCentimeterSeconds)
}

trait AreaTimeUnit extends UnitOfMeasure[AreaTime] {
  def apply[A](n: A)(using num: Numeric[A]): AreaTime = AreaTime(n, this)
}

object SquareMeterSeconds extends AreaTimeUnit with PrimaryUnit with SiUnit {
  val symbol = "m²‧s"
}

object SquareCentimeterSeconds extends AreaTimeUnit with UnitConverter {
  val symbol = "cm²‧s"
  val conversionFactor = 0.0001
}

object AreaTimeConversions {
  lazy val squareMeterSeconds: AreaTime = SquareMeterSeconds(1)
  lazy val squareCentimeterSeconds: AreaTime = SquareCentimeterSeconds(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def squareMeterSeconds: AreaTime = SquareMeterSeconds(n)
    def squareCentimeterSeconds: AreaTime = SquareCentimeterSeconds(n)
  }

  given AreaTimeNumeric: AbstractQuantityNumeric[AreaTime](AreaTime.primaryUnit) {}
}
