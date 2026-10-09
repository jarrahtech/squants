/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2013-2015, Gary Keorkunian                                       **
**                                                                      **
\*                                                                      */

package squants.motion

import squants._
import squants.space.Feet
import squants.time.{ SecondTimeDerivative, Seconds, TimeDerivative, TimeSquared }
import scala.util.Try

/**
 * Represents the third time derivative of position after Velocity and Acceleration
 *
 * @author  garyKeorkunian
 * @since   0.1
 *
 * @param value Double
 */
final class Jerk private (val value: Double, val unit: JerkUnit)
  extends Quantity[Jerk]
  with TimeDerivative[Acceleration]
  with SecondTimeDerivative[Velocity] {

  def dimension = Jerk

  protected[squants] def timeIntegrated: Acceleration = MetersPerSecondSquared(toMetersPerSecondCubed)
  protected[squants] def time: Time = Seconds(1)

  def *(that: TimeSquared): Velocity = this * that.time1 * that.time2

  def toMetersPerSecondCubed: Double = to(MetersPerSecondCubed)
  def toFeetPerSecondCubed: Double = to(FeetPerSecondCubed)
}

object Jerk extends Dimension[Jerk] {
  private[motion] def apply[A](n: A, unit: JerkUnit)(using num: Numeric[A]) = new Jerk(num.toDouble(n), unit)
  def apply(value: Any): Try[Jerk] = parse(value)
  def name = "Jerk"
  def primaryUnit = MetersPerSecondCubed
  def siUnit = MetersPerSecondCubed
  def units: Set[UnitOfMeasure[Jerk]] = Set(MetersPerSecondCubed, FeetPerSecondCubed)
}

trait JerkUnit extends UnitOfMeasure[Jerk] with UnitConverter {
  def apply[A](n: A)(using num: Numeric[A]): Jerk = Jerk(n, this)
}

object MetersPerSecondCubed extends JerkUnit with PrimaryUnit with SiUnit {
  val symbol = "m/s³"
}
object FeetPerSecondCubed extends JerkUnit {
  val symbol = "ft/s³"
  val conversionFactor: Double = Feet.conversionFactor / Meters.conversionFactor
}

object JerkConversions {
  lazy val meterPerSecondCubed: Jerk = MetersPerSecondCubed(1)
  lazy val footPerSecondCubed: Jerk = FeetPerSecondCubed(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def metersPerSecondCubed: Jerk = MetersPerSecondCubed(n)
    def feetPerSecondCubed: Jerk = FeetPerSecondCubed(n)
  }

  given JerkNumeric: AbstractQuantityNumeric[Jerk](Jerk.primaryUnit) {}
}
