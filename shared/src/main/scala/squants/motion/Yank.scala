/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2013-2015, Gary Keorkunian                                       **
**                                                                      **
\*                                                                      */

package squants.motion

import squants._
import squants.time.{ SecondTimeDerivative, TimeDerivative, TimeSquared }
import scala.util.Try

/**
 * @author  garyKeorkunian
 * @since   0.1
 *
 * @param value Double
 */
final class Yank private (val value: Double, val unit: YankUnit)
  extends Quantity[Yank]
  with TimeDerivative[Force]
  with SecondTimeDerivative[Momentum] {

  def dimension = Yank

  protected[squants] def timeIntegrated: Force = Newtons(toNewtonsPerSecond)
  protected[squants] def time: Time = Seconds(1)

  def *(that: TimeSquared): Momentum = this * that.time1 * that.time2

  def toNewtonsPerSecond: Double = to(NewtonsPerSecond)
}

object Yank extends Dimension[Yank] {
  private[motion] def apply[A](n: A, unit: YankUnit)(using num: Numeric[A]) = new Yank(num.toDouble(n), unit)
  def apply(value: Any): Try[Yank] = parse(value)
  def name = "Yank"
  def primaryUnit = NewtonsPerSecond
  def siUnit = NewtonsPerSecond
  def units: Set[UnitOfMeasure[Yank]] = Set(NewtonsPerSecond)
}

trait YankUnit extends UnitOfMeasure[Yank] with UnitConverter {
  def apply[A](n: A)(using num: Numeric[A]): Yank = Yank(n, this)
}

object NewtonsPerSecond extends YankUnit with PrimaryUnit with SiUnit {
  val symbol = "N/s"
}

object YankConversions {
  lazy val newtonPerSecond: Yank = NewtonsPerSecond(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def newtonsPerSecond: Yank = NewtonsPerSecond(n)
  }

  given YankNumeric: AbstractQuantityNumeric[Yank](Yank.primaryUnit) {}
}
