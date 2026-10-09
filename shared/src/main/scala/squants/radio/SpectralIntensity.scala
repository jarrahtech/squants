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
import squants.space.{ Meters, SquaredRadians }
import scala.util.Try

/**
 * @author  garyKeorkunian
 * @since   0.1
 *
 * @param value Double
 */
final class SpectralIntensity private (val value: Double, val unit: SpectralIntensityUnit)
  extends Quantity[SpectralIntensity] {

  def dimension = SpectralIntensity

  def *(that: Length): RadiantIntensity = WattsPerSteradian(this.toWattsPerSteradianPerMeter * that.toMeters)
  def /(that: RadiantIntensity): Length = Meters(this.toWattsPerSteradianPerMeter / that.toWattsPerSteradian)

  def toWattsPerSteradianPerMeter: Double = to(WattsPerSteradianPerMeter)
}

object SpectralIntensity extends Dimension[SpectralIntensity] {
  private[radio] def apply[A](n: A, unit: SpectralIntensityUnit)(using num: Numeric[A]) = new SpectralIntensity(num.toDouble(n), unit)
  def apply(value: Any): Try[SpectralIntensity] = parse(value)
  def name = "SpectralIntensity"
  def primaryUnit = WattsPerSteradianPerMeter
  def siUnit = WattsPerSteradianPerMeter
  def units: Set[UnitOfMeasure[SpectralIntensity]] = Set(WattsPerSteradianPerMeter)
}

trait SpectralIntensityUnit extends UnitOfMeasure[SpectralIntensity] {
  def apply[A](n: A)(using num: Numeric[A]): SpectralIntensity = SpectralIntensity(n, this)
}

object WattsPerSteradianPerMeter extends SpectralIntensityUnit with PrimaryUnit with SiUnit {
  val symbol: String = Watts.symbol + "/" + SquaredRadians.symbol + "/" + Meters.symbol
}

object SpectralIntensityConversions {
  lazy val wattPerSteradianPerMeter: SpectralIntensity = WattsPerSteradianPerMeter(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def wattsPerSteradianPerMeter: SpectralIntensity = WattsPerSteradianPerMeter(n)
  }

  given SpectralIntensityNumeric: AbstractQuantityNumeric[SpectralIntensity](SpectralIntensity.primaryUnit) {}
}

