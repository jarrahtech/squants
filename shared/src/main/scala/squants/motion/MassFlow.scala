/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2013-2015, Gary Keorkunian                                       **
**                                                                      **
\*                                                                      */

package squants.motion

import squants._
import squants.mass.{ Kilograms, Pounds }
import squants.time.{ Seconds, Time, TimeDerivative }
import scala.util.Try

/**
 * @author  garyKeorkunian
 * @since   0.1
 *
 * @param value Double
 */
final class MassFlow private (val value: Double, val unit: MassFlowUnit)
  extends Quantity[MassFlow]
  with TimeDerivative[Mass] {

  def dimension = MassFlow

  protected[squants] def timeIntegrated: Mass = Kilograms(toKilogramsPerSecond)
  protected[squants] def time: Time = Seconds(1)

  def toKilogramsPerSecond: Double = to(KilogramsPerSecond)
  def toPoundsPerSecond: Double = to(PoundsPerSecond)
  def toPoundsPerHour: Double = to(PoundsPerHour)
  def toKilopoundsPerHour: Double = to(KilopoundsPerHour)
  def toMegapoundsPerHour: Double = to(MegapoundsPerHour)
}

object MassFlow extends Dimension[MassFlow] {
  private[motion] def apply[A](n: A, unit: MassFlowUnit)(using num: Numeric[A]) = new MassFlow(num.toDouble(n), unit)
  def apply(value: Any): Try[MassFlow] = parse(value)
  def name = "MassFlow"
  def primaryUnit = KilogramsPerSecond
  def siUnit = KilogramsPerSecond
  def units: Set[UnitOfMeasure[MassFlow]] = Set(KilogramsPerSecond, PoundsPerSecond, PoundsPerHour, KilopoundsPerHour, MegapoundsPerHour)
}

trait MassFlowUnit extends UnitOfMeasure[MassFlow] with UnitConverter {
  def apply[A](n: A)(using num: Numeric[A]): MassFlow = MassFlow(n, this)
}

object KilogramsPerSecond extends MassFlowUnit with PrimaryUnit with SiUnit {
  val symbol = "kg/s"
}

object PoundsPerSecond extends MassFlowUnit {
  val symbol = "lb/s"
  val conversionFactor: Double = Pounds.conversionFactor / Kilograms.conversionFactor
}

object PoundsPerHour extends MassFlowUnit {
  val symbol = "lb/hr"
  val conversionFactor: Double = PoundsPerSecond.conversionFactor / Time.SecondsPerHour
}

object KilopoundsPerHour extends MassFlowUnit {
  val symbol = "klb/hr"
  val conversionFactor: Double = PoundsPerHour.conversionFactor * MetricSystem.Kilo
}

object MegapoundsPerHour extends MassFlowUnit {
  val symbol = "Mlb/hr"
  val conversionFactor: Double = PoundsPerHour.conversionFactor * MetricSystem.Mega
}

object MassFlowConversions {
  lazy val kilogramPerSecond: MassFlow = KilogramsPerSecond(1)
  lazy val poundPerSecond: MassFlow = PoundsPerSecond(1)
  lazy val poundPerHour: MassFlow = PoundsPerHour(1)
  lazy val kilopoundPerHour: MassFlow = KilopoundsPerHour(1)
  lazy val megapoundPerHour: MassFlow = MegapoundsPerHour(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def kilogramsPerSecond: MassFlow = KilogramsPerSecond(n)
    def poundsPerSecond: MassFlow = PoundsPerSecond(n)
    def poundsPerHour: MassFlow = PoundsPerHour(n)
    def kilopoundsPerHour: MassFlow = KilopoundsPerHour(n)
    def megapoundsPerHour: MassFlow = MegapoundsPerHour(n)
  }

  given MassFlowNumeric: AbstractQuantityNumeric[MassFlow](MassFlow.primaryUnit) {}
}
