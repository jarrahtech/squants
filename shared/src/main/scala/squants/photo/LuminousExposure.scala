/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2013-2015, Gary Keorkunian                                       **
**                                                                      **
\*                                                                      */

package squants.photo

import squants._
import squants.time.TimeIntegral
import scala.util.Try

/**
 * @author  garyKeorkunian
 * @since   0.1
 *
 * @param value value in [[squants.photo.LuxSeconds]]
 */
final class LuminousExposure private (val value: Double, val unit: LuminousExposureUnit)
  extends Quantity[LuminousExposure]
  with TimeIntegral[Illuminance] {

  def dimension = LuminousExposure

  protected def timeDerived: Illuminance = Lux(toLuxSeconds)
  protected[squants] def time: Time = Seconds(1)

  def toLuxSeconds: Double = to(LuxSeconds)
}

object LuminousExposure extends Dimension[LuminousExposure] {
  private[photo] def apply[A](n: A, unit: LuminousExposureUnit)(using num: Numeric[A]) = new LuminousExposure(num.toDouble(n), unit)
  def apply(value: Any): Try[LuminousExposure] = parse(value)
  def name = "LuminousExposure"
  def primaryUnit = LuxSeconds
  def siUnit = LuxSeconds
  def units: Set[UnitOfMeasure[LuminousExposure]] = Set(LuxSeconds)
}

trait LuminousExposureUnit extends UnitOfMeasure[LuminousExposure] with UnitConverter {
  def apply[A](n: A)(using num: Numeric[A]): LuminousExposure = LuminousExposure(n, this)
}

object LuxSeconds extends LuminousExposureUnit with PrimaryUnit with SiUnit {
  val symbol = "lx⋅s"
}

object LuminousExposureConversions {
  lazy val luxSecond: LuminousExposure = LuxSeconds(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def luxSeconds: LuminousExposure = LuxSeconds(n)
  }

  given LuminousExposureNumeric: AbstractQuantityNumeric[LuminousExposure](LuminousExposure.primaryUnit) {}
}