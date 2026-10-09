/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2013-2015, Gary Keorkunian                                       **
**                                                                      **
\*                                                                      */

package squants.motion

import squants.{ AbstractQuantityNumeric, Dimension, PrimaryUnit, Quantity, SiUnit, UnitOfMeasure }
import squants.mass.{ Kilograms, Mass }
import squants.time.{ SecondTimeIntegral, Seconds, TimeIntegral, TimeSquared }
import scala.util.Try

/**
 * @author  garyKeorkunian
 * @since   0.1
 *
 * @param value Double
 */
final class Momentum private (val value: Double, val unit: MomentumUnit)
  extends Quantity[Momentum]
  with TimeIntegral[Force]
  with SecondTimeIntegral[Yank] {

  def dimension = Momentum

  protected def timeDerived: Force = Newtons(toNewtonSeconds)
  protected def time: squants.Time = Seconds(1)

  def /(that: Velocity): Mass = Kilograms(this.toNewtonSeconds / that.toMetersPerSecond)
  def /(that: Mass): Velocity = MetersPerSecond(this.toNewtonSeconds / that.toKilograms)

  def /(that: TimeSquared): Yank = this / that.time1 / that.time2
  def /(that: Yank): TimeSquared = (this / that.timeIntegrated) * time

  def toNewtonSeconds: Double = to(NewtonSeconds)
}

object Momentum extends Dimension[Momentum] {
  private[motion] def apply[A](n: A, unit: MomentumUnit)(using num: Numeric[A]) = new Momentum(num.toDouble(n), unit)
  def apply(m: Mass, v: Velocity): Momentum = NewtonSeconds(m.toKilograms * v.toMetersPerSecond)
  def apply(value: Any): Try[Momentum] = parse(value)
  def name = "Momentum"
  def primaryUnit = NewtonSeconds
  def siUnit = NewtonSeconds
  def units: Set[UnitOfMeasure[Momentum]] = Set(NewtonSeconds)
}

trait MomentumUnit extends UnitOfMeasure[Momentum] {
  def apply[A](n: A)(using num: Numeric[A]): Momentum = Momentum(n, this)
}

object NewtonSeconds extends MomentumUnit with PrimaryUnit with SiUnit {
  val symbol = "Ns"
}

object MomentumConversions {
  lazy val newtonSecond: Momentum = NewtonSeconds(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def newtonSeconds: Momentum = NewtonSeconds(n)
  }

  given MomentumNumeric: AbstractQuantityNumeric[Momentum](Momentum.primaryUnit) {}
}
