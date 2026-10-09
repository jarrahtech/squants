package squants.motion

import squants.mass.MomentOfInertia
import squants.space._
import squants.time.{ Seconds, Time, TimeDerivative }
import squants.{ AbstractQuantityNumeric, Dimension, Length, PrimaryUnit, Quantity, SiUnit, UnitConverter, UnitOfMeasure }
import scala.util.Try

/**
 *
 * @author paxelord
 * @since 1.3
 *
 * @param value Double
 */
final class AngularAcceleration private (val value: Double, val unit: AngularAccelerationUnit)
  extends Quantity[AngularAcceleration] with TimeDerivative[AngularVelocity] {

  def dimension = AngularAcceleration

  def toRadiansPerSecondSquared: Double = to(RadiansPerSecondSquared)
  def toDegreesPerSecondSquared: Double = to(DegreesPerSecondSquared)
  def toGradsPerSecondSquared: Double = to(GradiansPerSecondSquared)
  def toTurnsPerSecondSquared: Double = to(TurnsPerSecondSquared)
  def toArcminutesPerSecondSquared: Double = to(ArcminutesPerSecondSquared)
  def toArcsecondsPerSecondSquared: Double = to(ArcsecondsPerSecondSquared)

  /**
   * linear acceleration of an object rotating with this angular acceleration
   * and the given radius from the center of rotation
   * @param radius the distance from the center of rotation
   * @return linear acceleration with given angular acceleration and radius
   */
  infix def onRadius(radius: Length): Acceleration = radius * toRadiansPerSecondSquared / Seconds(1).squared

  def *(that: MomentOfInertia): Torque = {
    NewtonMeters(this.toRadiansPerSecondSquared * that.toKilogramsMetersSquared)
  }

  override protected[squants] def timeIntegrated: AngularVelocity = RadiansPerSecond(toRadiansPerSecondSquared)

  override protected[squants] def time: Time = Seconds(1)
}

object AngularAcceleration extends Dimension[AngularAcceleration] {
  private[motion] def apply[A](n: A, unit: AngularAccelerationUnit)(using num: Numeric[A]) = new AngularAcceleration(num.toDouble(n), unit)
  def apply(value: Any): Try[AngularAcceleration] = parse(value)
  def name = "AngularAcceleration"
  def primaryUnit = RadiansPerSecondSquared
  def siUnit = RadiansPerSecondSquared
  def units: Set[UnitOfMeasure[AngularAcceleration]] = Set(
    RadiansPerSecondSquared,
    DegreesPerSecondSquared,
    GradiansPerSecondSquared,
    TurnsPerSecondSquared,
    ArcminutesPerSecondSquared,
    ArcsecondsPerSecondSquared)
}

trait AngularAccelerationUnit extends UnitOfMeasure[AngularAcceleration] with UnitConverter {
  def apply[A](n: A)(using num: Numeric[A]): AngularAcceleration = {
    AngularAcceleration(num.toDouble(n), this)
  }

  val conversionFactor: Double
}

object RadiansPerSecondSquared extends AngularAccelerationUnit with PrimaryUnit with SiUnit {
  val symbol: String = Radians.symbol + "/s²"
}

object DegreesPerSecondSquared extends AngularAccelerationUnit {
  val symbol: String = Degrees.symbol + "/s²"
  val conversionFactor = Degrees.conversionFactor
}

object GradiansPerSecondSquared extends AngularAccelerationUnit {
  val symbol: String = Gradians.symbol + "/s²"
  val conversionFactor = Gradians.conversionFactor
}

object TurnsPerSecondSquared extends AngularAccelerationUnit {
  val symbol: String = Turns.symbol + "/s²"
  val conversionFactor = Turns.conversionFactor
}

object ArcminutesPerSecondSquared extends AngularAccelerationUnit {
  val symbol: String = Arcminutes.symbol + "/s²"
  val conversionFactor = Arcminutes.conversionFactor
}

object ArcsecondsPerSecondSquared extends AngularAccelerationUnit {
  val symbol: String = Arcseconds.symbol + "/s²"
  val conversionFactor = Arcseconds.conversionFactor
}

object AngularAccelerationConversions {
  lazy val radianPerSecondSquared: AngularAcceleration = RadiansPerSecondSquared(1)
  lazy val degreePerSecondSquared: AngularAcceleration = DegreesPerSecondSquared(1)
  lazy val gradPerSecondSquared: AngularAcceleration = GradiansPerSecondSquared(1)
  lazy val turnPerSecondSquared: AngularAcceleration = TurnsPerSecondSquared(1)

  extension [A](n: A) {
    def radiansPerSecondSquared(using num: Numeric[A]): AngularAcceleration = RadiansPerSecondSquared(n)
    def degreesPerSecondSquared(using num: Numeric[A]): AngularAcceleration = DegreesPerSecondSquared(n)
    def gradsPerSecondSquared(using num: Numeric[A]): AngularAcceleration = GradiansPerSecondSquared(n)
    def turnsPerSecondSquared(using num: Numeric[A]): AngularAcceleration = TurnsPerSecondSquared(n)
  }

  given AngularAccelerationNumeric: AbstractQuantityNumeric[AngularAcceleration](AngularAcceleration.primaryUnit) {}
}
