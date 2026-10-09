package squants.mass

import squants.motion.{ AngularAcceleration, NewtonMeters, Torque }
import squants.space.{ Feet, Meters }
import squants.{ AbstractQuantityNumeric, Dimension, Length, PrimaryUnit, Quantity, SiBaseUnit, UnitConverter, UnitOfMeasure }
import scala.util.Try

/**
 *
 * @author paxelord
 * @since 1.3
 *
 * @param value Double
 */
final class MomentOfInertia private (val value: Double, val unit: MomentOfInertiaUnit)
  extends Quantity[MomentOfInertia] {

  def dimension = MomentOfInertia

  def toKilogramsMetersSquared: Double = to(KilogramsMetersSquared)
  def toPoundsSquareFeet: Double = to(PoundsSquareFeet)

  def *(angularAcceleration: AngularAcceleration): Torque = {
    val radiansPerSecondSquared = angularAcceleration.toRadiansPerSecondSquared

    NewtonMeters(toKilogramsMetersSquared * radiansPerSecondSquared)
  }

  /**
   * For a point mass with the given MomentOfInertia rotating with a center of
   * rotation at the given radius, return the mass of the point mass
   * @param radius distance to axis of rotation
   * @return mass of point mass with given radius and MomentOfInertia
   */
  infix def atCenter(radius: Length): Mass = {
    Kilograms(toKilogramsMetersSquared / radius.squared.toSquareMeters)
  }
}

object MomentOfInertia extends Dimension[MomentOfInertia] {
  private[mass] def apply[A](n: A, unit: MomentOfInertiaUnit)(using num: Numeric[A]) = new MomentOfInertia(num.toDouble(n), unit)
  def apply(value: Any): Try[MomentOfInertia] = parse(value)
  def name = "MomentOfInertia"
  def primaryUnit = KilogramsMetersSquared
  def siUnit = KilogramsMetersSquared
  def units: Set[UnitOfMeasure[MomentOfInertia]] = Set(KilogramsMetersSquared, PoundsSquareFeet)
}

trait MomentOfInertiaUnit extends UnitOfMeasure[MomentOfInertia] with UnitConverter {
  def apply[A](n: A)(using num: Numeric[A]): MomentOfInertia = {
    MomentOfInertia(num.toDouble(n), this)
  }
}

object KilogramsMetersSquared extends MomentOfInertiaUnit with PrimaryUnit with SiBaseUnit {
  val symbol: String = Kilograms.symbol + "‧" + Meters.symbol + "²"
}

object PoundsSquareFeet extends MomentOfInertiaUnit {
  val symbol: String = Pounds.symbol + "‧" + Feet.symbol + "²"
  val conversionFactor: Double = Pounds.conversionFactor * math.pow(Feet.conversionFactor, 2D)
}

object MomentOfInertiaConversions {
  lazy val kilogramMetersSquared: MomentOfInertia = KilogramsMetersSquared(1)
  lazy val poundSquareFeet: MomentOfInertia = PoundsSquareFeet(1)

  extension [A](n: A) {
    def kilogramMetersSquared(using num: Numeric[A]): MomentOfInertia = KilogramsMetersSquared(n)
    def poundSquareFeet(using num: Numeric[A]): MomentOfInertia = PoundsSquareFeet(n)
  }

  given MomentOfInertiaNumeric: AbstractQuantityNumeric[MomentOfInertia](MomentOfInertia.primaryUnit) {}
}