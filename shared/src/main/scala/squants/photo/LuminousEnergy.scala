/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2013-2015, Gary Keorkunian                                       **
**                                                                      **
\*                                                                      */
package squants.photo

import squants._
import squants.time.{ Seconds, TimeIntegral }
import scala.util.Try

/**
 * @author  garyKeorkunian
 * @since   0.1
 *
 * @param value value in [[squants.photo.LumenSeconds]]
 */
final class LuminousEnergy private (val value: Double, val unit: LuminousEnergyUnit)
  extends Quantity[LuminousEnergy]
  with TimeIntegral[LuminousFlux] {

  def dimension = LuminousEnergy

  protected def timeDerived: LuminousFlux = Lumens(toLumenSeconds)
  protected[squants] def time: Time = Seconds(1)

  def toLumenSeconds: Double = to(LumenSeconds)
}

object LuminousEnergy extends Dimension[LuminousEnergy] {
  private[photo] def apply[A](n: A, unit: LuminousEnergyUnit)(using num: Numeric[A]) = new LuminousEnergy(num.toDouble(n), unit)
  def apply(value: Any): Try[LuminousEnergy] = parse(value)
  def name = "LuminousEnergy"
  def primaryUnit = LumenSeconds
  def siUnit = LumenSeconds
  def units: Set[UnitOfMeasure[LuminousEnergy]] = Set(LumenSeconds)
}

trait LuminousEnergyUnit extends UnitOfMeasure[LuminousEnergy] with UnitConverter {
  def apply[A](n: A)(using num: Numeric[A]): LuminousEnergy = LuminousEnergy(num.toDouble(n), this)
}

object LumenSeconds extends LuminousEnergyUnit with PrimaryUnit with SiUnit {
  val symbol = "lm⋅s"
}

object LuminousEnergyConversions {
  lazy val lumenSecond: LuminousEnergy = LumenSeconds(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def lumenSeconds: LuminousEnergy = LumenSeconds(n)
  }

  given LuminousEnergyNumeric: AbstractQuantityNumeric[LuminousEnergy](LuminousEnergy.primaryUnit) {}
}
