/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2013-2015, Gary Keorkunian                                       **
**                                                                      **
\*                                                                      */

package squants.energy

import squants._
import squants.motion.{CubicMetersPerSecondSquared, GravitationalParameter, MetersPerSecond}
import scala.util.Try

/**
 * @author  garyKeorkunian
 * @since   0.1
 *
 * @param value value in [[squants.energy.Grays]]
 */
final class SpecificEnergy private (val value: Double, val unit: SpecificEnergyUnit) extends Quantity[SpecificEnergy] {

  def dimension = SpecificEnergy

  def *(that: Mass): Energy = Joules(this.toGrays * that.toKilograms)
  /** The gravitational parameter that has this specific energy at distance `that`: `mu = e * r`. */
  def *(that: Length): GravitationalParameter = CubicMetersPerSecondSquared(this.toGrays * that.toMeters)
  /** The velocity whose kinetic energy per unit mass is this: `sqrt(e)` in metres per second (NaN if negative). */
  def squareRoot: Velocity = MetersPerSecond(math.sqrt(toGrays))
  def /(that: Time) = ??? // returns AbsorbedEnergyRate

  def toGrays: Double = to(Grays)
  def toRads: Double = to(Rads)
  def toErgsPerGram: Double = to(ErgsPerGram)
}

object SpecificEnergy extends Dimension[SpecificEnergy] {
  private[energy] def apply[A](n: A, unit: SpecificEnergyUnit)(using num: Numeric[A]) = new SpecificEnergy(num.toDouble(n), unit)
  def apply(value: Any): Try[SpecificEnergy] = parse(value)
  def name = "SpecificEnergy"
  def primaryUnit = Grays
  def siUnit = Grays
  def units: Set[UnitOfMeasure[SpecificEnergy]] = Set(Grays, Rads, ErgsPerGram)
}

trait SpecificEnergyUnit extends UnitOfMeasure[SpecificEnergy] {
  def apply[A](n: A)(using num: Numeric[A]): SpecificEnergy = SpecificEnergy(n, this)
}

object Grays extends SpecificEnergyUnit with PrimaryUnit with SiUnit {
  val symbol = "Gy"
}

object Rads extends SpecificEnergyUnit with UnitConverter {
  val symbol = "rad"
  val conversionFactor = 0.01
}

object ErgsPerGram extends SpecificEnergyUnit with UnitConverter {
  val symbol = "erg/g"
  val conversionFactor = 0.0001
}

object SpecificEnergyConversions {
  lazy val gray: SpecificEnergy = Grays(1)
  lazy val rad: SpecificEnergy = Rads(1)
  lazy val ergsPerGram: SpecificEnergy = ErgsPerGram(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def grays: SpecificEnergy = Grays(n)
    def rads: SpecificEnergy = Rads(n)
    def ergsPerGram: SpecificEnergy = ErgsPerGram(n)
  }

  given SpecificEnergyNumeric: AbstractQuantityNumeric[SpecificEnergy](SpecificEnergy.primaryUnit) {}
}
