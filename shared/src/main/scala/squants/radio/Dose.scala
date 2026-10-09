/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2013-2015, Gary Keorkunian                                       **
**                                                                      **
\*                                                                      */

package squants.radio

import squants._
import squants.mass.Mass
import squants.energy.{ Energy, Joules }
import squants.time.Time
import scala.util.Try

/**
 * Its important to note that while Dose and SpecificEnergy are simliar
 * measures (both are energy/mass dimensions), they are decidedly different
 * and no conversions between the two dimensions should be directly possible
 * through implicit conversions EVER.  This is because SpecificEnergy is used to
 * measure absorbed dose which just measures energy deposited into a mass of
 * material while Dose is used to measure equivalent/effective/committed doses
 * which are measures of the damage done to biological tissues.  Since this
 * is an easy and disastrous mistake to make, it is critical that Squants
 * doesn't allow any sort of magic conversions that allow this mistake.
 * @author  Hunter Payne
 *
 */
final class Dose private (val value: Double, val unit: DoseUnit) extends Quantity[Dose] {

  def dimension = Dose

  def *(that: Mass): Energy = Joules(this.toSieverts * that.toKilograms)
  def /(that: Time) = ??? // returns AbsorbedEnergyRate

  def toSieverts: Double = to(Sieverts)
  def toRems: Double = to(Rems)
}

object Dose extends Dimension[Dose] {
  private[radio] def apply[A](n: A, unit: DoseUnit)(using num: Numeric[A]) = new Dose(num.toDouble(n), unit)
  def apply(value: Any): Try[Dose] = parse(value)
  def name = "Dose"
  def primaryUnit = Sieverts
  def siUnit = Sieverts
  def units: Set[UnitOfMeasure[Dose]] = Set(Sieverts, Rems)
}

trait DoseUnit extends UnitOfMeasure[Dose] {
  def apply[A](n: A)(using num: Numeric[A]): Dose = Dose(n, this)
}

object Rems extends DoseUnit with UnitConverter {
  val symbol = "rem"
  val conversionFactor = 0.01
}

object Sieverts extends DoseUnit with PrimaryUnit with SiUnit {
  val symbol = "Sv"
}

object DoseConversions {
  lazy val sievert: Dose = Sieverts(1)
  lazy val rem: Dose = Rems(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def sieverts: Dose = Sieverts(n)
    def rems: Dose = Rems(n)
  }

  given DoseNumeric: AbstractQuantityNumeric[Dose](Dose.primaryUnit) {}
}
