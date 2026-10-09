/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2013-2015, Gary Keorkunian                                       **
**                                                                      **
\*                                                                      */

package squants.motion

import squants._
import squants.space.{ Degrees, Gradians, Turns }
import squants.time.{ TimeDerivative, TimeIntegral }
import scala.util.Try

/**
 * @author  garyKeorkunian
 * @since   0.1
 *
 * @param value Double
 *
 */
final class AngularVelocity private (val value: Double, val unit: AngularVelocityUnit)
  extends Quantity[AngularVelocity] with TimeDerivative[Angle] with TimeIntegral[AngularAcceleration] {
  def dimension = AngularVelocity

  def toRadiansPerSecond: Double = to(RadiansPerSecond)
  def toDegreesPerSecond: Double = to(DegreesPerSecond)
  @deprecated(message = "Potentially confusing naming. Use toGradiansPerSecond instead.", since = "Squants 1.3")
  def toGradsPerSecond: Double = to(GradiansPerSecond)
  def toGradiansPerSecond: Double = to(GradiansPerSecond)
  def toTurnsPerSecond: Double = to(TurnsPerSecond)

  /**
   * linear velocity of an object rotating with this angular velocity
   * and the given radius from the center of rotation
   * @param radius the distance from the center of rotation
   * @return linear velocity with given angular velocity and radius
   */
  infix def onRadius(radius: Length): Velocity = toRadiansPerSecond * radius / Seconds(1)

  protected[squants] def timeIntegrated: Angle = Radians(toRadiansPerSecond)

  protected[squants] def timeDerived: AngularAcceleration = RadiansPerSecondSquared(toRadiansPerSecond)

  protected[squants] def time: Time = Seconds(1)
}

object AngularVelocity extends Dimension[AngularVelocity] {
  private[motion] def apply[A](n: A, unit: AngularVelocityUnit)(using num: Numeric[A]) = new AngularVelocity(num.toDouble(n), unit)
  def apply(value: Any): Try[AngularVelocity] = parse(value)
  def name = "AngularVelocity"
  def primaryUnit = RadiansPerSecond
  def siUnit = RadiansPerSecond
  def units: Set[UnitOfMeasure[AngularVelocity]] = Set(RadiansPerSecond, DegreesPerSecond, GradiansPerSecond, TurnsPerSecond)
}

trait AngularVelocityUnit extends UnitOfMeasure[AngularVelocity] with UnitConverter {
  def apply[A](n: A)(using num: Numeric[A]): AngularVelocity = AngularVelocity(n, this)
}

object RadiansPerSecond extends AngularVelocityUnit with PrimaryUnit with SiUnit {
  val symbol = "rad/s"
}

object DegreesPerSecond extends AngularVelocityUnit {
  val symbol = "°/s"
  val conversionFactor: Double = Degrees.conversionFactor / Radians.conversionFactor
}

object GradiansPerSecond extends AngularVelocityUnit {
  val symbol = "grad/s"
  val conversionFactor: Double = Gradians.conversionFactor / Radians.conversionFactor
}

@deprecated(message = "Potentially confusing naming. Use GradiansPerSecond instead.", since = "Squants 1.3")
object GradsPerSecond extends AngularVelocityUnit {
  val symbol = "grad/s"
  val conversionFactor: Double = Gradians.conversionFactor / Radians.conversionFactor
}

object TurnsPerSecond extends AngularVelocityUnit {
  val symbol = "turns/s"
  val conversionFactor: Double = Turns.conversionFactor / Radians.conversionFactor
}

object AngularVelocityConversions {
  lazy val radianPerSecond: AngularVelocity = RadiansPerSecond(1)
  lazy val degreePerSecond: AngularVelocity = DegreesPerSecond(1)
  lazy val gradPerSecond: AngularVelocity = GradiansPerSecond(1)
  lazy val gradiansPerSecond: AngularVelocity = GradiansPerSecond(1)
  lazy val turnPerSecond: AngularVelocity = TurnsPerSecond(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def radiansPerSecond: AngularVelocity = RadiansPerSecond(n)
    def degreesPerSecond: AngularVelocity = DegreesPerSecond(n)
    @deprecated(message = "Potentially confusing naming. Use gradiansPerSecond instead.", since = "Squants 1.3")
    def gradsPerSecond: AngularVelocity = GradiansPerSecond(n)
    def gradiansPerSecond: AngularVelocity = GradiansPerSecond(n)
    def turnsPerSecond: AngularVelocity = TurnsPerSecond(n)
  }

  given AngularVelocityNumeric: AbstractQuantityNumeric[AngularVelocity](AngularVelocity.primaryUnit) {}
}
