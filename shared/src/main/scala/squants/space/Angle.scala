/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2013-2015, Gary Keorkunian                                       **
**                                                                      **
\*                                                                      */

package squants.space

import squants._
import squants.motion.{ AngularVelocity, RadiansPerSecond }
import squants.time.{ Time, TimeIntegral }
import scala.util.Try

/**
 * @author  garyKeorkunian
 * @since   0.1
 *
 * @param value value in [[squants.space.Radians]]
 */
final class Angle private (val value: Double, val unit: AngleUnit)
  extends Quantity[Angle] with TimeIntegral[AngularVelocity] {

  def dimension = Angle

  def toRadians: Double = to(Radians)
  def toDegrees: Double = to(Degrees)
  def toGradians: Double = to(Gradians)
  def toTurns: Double = to(Turns)
  def toArcminutes: Double = to(Arcminutes)
  def toArcseconds: Double = to(Arcseconds)

  def sin: Double = math.sin(toRadians)
  def cos: Double = math.cos(toRadians)
  def tan: Double = math.tan(toRadians)
  def asin: Double = math.asin(toRadians)
  def acos: Double = math.acos(toRadians)

  /**
   * length of the arc traveled by a point on the rim of a circle with this
   * angle traveled and the given (constant) radius from the center of
   * rotation
   * @param radius the distance from the center of rotation
   * @return arc length with given arc measure and radius
   */
  infix def onRadius(radius: Length): Length = toRadians * radius

  protected def timeDerived: AngularVelocity = RadiansPerSecond(toRadians)

  override protected def time: Time = Seconds(1)
}

object Angle extends Dimension[Angle] {
  private[space] def apply[A](n: A, unit: AngleUnit)(using num: Numeric[A]) = new Angle(num.toDouble(n), unit)
  def apply(value: Any): Try[Angle] = parse(value)
  def name = "Angle"
  def primaryUnit = Radians
  def siUnit = Radians
  def units: Set[UnitOfMeasure[Angle]] = Set(Radians, Degrees, Gradians, Turns, Arcminutes, Arcseconds)
}

trait AngleUnit extends UnitOfMeasure[Angle] with UnitConverter {
  def apply[A](n: A)(using num: Numeric[A]): Angle = Angle(n, this)
}

object Radians extends AngleUnit with PrimaryUnit with SiUnit {
  val symbol = "rad"
}

object Degrees extends AngleUnit {
  val symbol = "°"
  val conversionFactor: Double = math.Pi / 180d
}

object Gradians extends AngleUnit {
  val symbol = "grad"
  val conversionFactor: Double = Turns.conversionFactor / 400d
}

object Turns extends AngleUnit {
  val symbol = "turns"
  val conversionFactor: Double = 2 * math.Pi
}

object Arcminutes extends AngleUnit {
  val symbol = "amin"
  val conversionFactor: Double = math.Pi / 10800d
}

object Arcseconds extends AngleUnit {
  val symbol = "asec"
  val conversionFactor: Double = 1d / Time.SecondsPerMinute * Arcminutes.conversionFactor
}

object AngleConversions {
  lazy val radian: Angle = Radians(1)
  lazy val degree: Angle = Degrees(1)
  lazy val gradian: Angle = Gradians(1)
  lazy val turn: Angle = Turns(1)
  lazy val arcminute: Angle = Arcminutes(1)
  lazy val arcsecond: Angle = Arcseconds(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def radians: Angle = Radians(n)
    def degrees: Angle = Degrees(n)
    def gradians: Angle = Gradians(n)
    def turns: Angle = Turns(n)
    def arcminutes: Angle = Arcminutes(n)
    def arcseconds: Angle = Arcseconds(n)
  }

  given AngleNumeric: AbstractQuantityNumeric[Angle](Angle.primaryUnit) {}
}
