/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2013-2015, Gary Keorkunian                                       **
**                                                                      **
\*                                                                      */

package squants.radio

import squants._
import squants.energy.Watts
import squants.space.Meters
import scala.util.Try

/**
 * @author  garyKeorkunian
 * @since   0.1
 *
 * @param value Double
 */
final class SpectralPower private (val value: Double, val unit: SpectralPowerUnit)
  extends Quantity[SpectralPower] {

  def dimension = SpectralPower

  def *(that: Length): Power = Watts(this.toWattsPerMeter * that.toMeters)
  def /(that: Power): Length = Meters(this.toWattsPerMeter / that.toWatts)

  def toWattsPerMeter = value
}

object SpectralPower extends Dimension[SpectralPower] {
  private[radio] def apply[A](n: A, unit: SpectralPowerUnit)(using num: Numeric[A]) = new SpectralPower(num.toDouble(n), unit)
  def apply(value: Any): Try[SpectralPower] = parse(value)
  def name = "SpectralPower"
  def primaryUnit = WattsPerMeter
  def siUnit = WattsPerMeter
  def units: Set[UnitOfMeasure[SpectralPower]] = Set(WattsPerMeter)
}

trait SpectralPowerUnit extends UnitOfMeasure[SpectralPower] {
  def apply[A](n: A)(using num: Numeric[A]): SpectralPower = SpectralPower(n, this)
}

object WattsPerMeter extends SpectralPowerUnit with PrimaryUnit with SiUnit {
  val symbol: String = Watts.symbol + "/" + Meters.symbol
}

object SpectralPowerConversions {
  lazy val wattPerMeter: SpectralPower = WattsPerMeter(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def wattsPerMeter: SpectralPower = WattsPerMeter(n)
  }

  given SpectralPowerNumeric: AbstractQuantityNumeric[SpectralPower](SpectralPower.primaryUnit) {}
}

