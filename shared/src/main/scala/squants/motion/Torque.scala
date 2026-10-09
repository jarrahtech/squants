package squants.motion

import squants.mass.{ MomentOfInertia, Pounds }
import squants.space.{ Feet, Meters }
import squants.{ AbstractQuantityNumeric, Dimension, PrimaryUnit, Quantity, SiBaseUnit, UnitConverter, UnitOfMeasure }
import scala.util.Try

/**
 *
 * @author paxelord
 * @since 1.3
 *
 * @param value Double
 */
final class Torque private (val value: Double, val unit: TorqueUnit)
  extends Quantity[Torque] {

  def dimension = Torque

  def toNewtonMeters: Double = to(NewtonMeters)
  def toPoundFeet: Double = to(PoundFeet)

  def /(that: MomentOfInertia): AngularAcceleration = {
    RadiansPerSecondSquared(toNewtonMeters / that.toKilogramsMetersSquared)
  }
}

object Torque extends Dimension[Torque] {
  private[motion] def apply[A](n: A, unit: TorqueUnit)(using num: Numeric[A]) = new Torque(num.toDouble(n), unit)
  def apply(value: Any): Try[Torque] = parse(value)
  def name = "Torque"
  def primaryUnit = NewtonMeters
  def siUnit = NewtonMeters
  def units: Set[UnitOfMeasure[Torque]] = Set(NewtonMeters, PoundFeet)
}

trait TorqueUnit extends UnitOfMeasure[Torque] with UnitConverter {
  def apply[A](n: A)(using num: Numeric[A]): Torque = {
    Torque(num.toDouble(n), this)
  }
}

object NewtonMeters extends TorqueUnit with PrimaryUnit with SiBaseUnit {
  val symbol: String = Newtons.symbol + "‧" + Meters.symbol
}

object PoundFeet extends TorqueUnit {
  val symbol: String = Pounds.symbol + "‧" + Feet.symbol
  val conversionFactor: Double = PoundForce.conversionFactor * Feet.conversionFactor
}

object TorqueConversions {
  lazy val newtonMeters: Torque = NewtonMeters(1)
  lazy val poundFeet: Torque = PoundFeet(1)

  extension [A](n: A) {
    def newtonMeters(using num: Numeric[A]): Torque = NewtonMeters(n)
    def poundFeet(using num: Numeric[A]): Torque = PoundFeet(n)
  }

  given TorqueNumeric: AbstractQuantityNumeric[Torque](Torque.primaryUnit) {}
}
