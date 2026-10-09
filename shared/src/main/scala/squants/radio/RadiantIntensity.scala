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
import squants.space.{ SquareMeters, SquaredRadians }
import scala.util.Try

/**
 * @author  garyKeorkunian
 * @since   0.1
 *
 * @param value Double
 */
final class RadiantIntensity private (val value: Double, val unit: RadiantIntensityUnit)
  extends Quantity[RadiantIntensity] {

  def dimension = RadiantIntensity

  def *(that: SolidAngle): Power = Watts(this.toWattsPerSteradian * that.toSquaredRadians)
  def /(that: Power): SolidAngle = SquareRadians(this.toWattsPerSteradian / that.toWatts)
  def /(that: Length): SpectralIntensity = WattsPerSteradianPerMeter(this.toWattsPerSteradian / that.toMeters)
  def /(that: SpectralIntensity): Length = Meters(this.toWattsPerSteradian / that.toWattsPerSteradianPerMeter)
  def /(that: Area): Radiance = WattsPerSteradianPerSquareMeter(this.toWattsPerSteradian / that.toSquareMeters)
  def /(that: Radiance): Area = SquareMeters(this.toWattsPerSteradian / that.toWattsPerSteradianPerSquareMeter)

  def toWattsPerSteradian: Double = to(WattsPerSteradian)
}

object RadiantIntensity extends Dimension[RadiantIntensity] {
  private[radio] def apply[A](n: A, unit: RadiantIntensityUnit)(using num: Numeric[A]) = new RadiantIntensity(num.toDouble(n), unit)
  def apply(value: Any): Try[RadiantIntensity] = parse(value)
  def name = "RadiantIntensity"
  def primaryUnit = WattsPerSteradian
  def siUnit = WattsPerSteradian
  def units: Set[UnitOfMeasure[RadiantIntensity]] = Set(WattsPerSteradian)
}

trait RadiantIntensityUnit extends UnitOfMeasure[RadiantIntensity] {
  def apply[A](n: A)(using num: Numeric[A]): RadiantIntensity = RadiantIntensity(n, this)
}

object WattsPerSteradian extends RadiantIntensityUnit with PrimaryUnit with SiUnit {
  val symbol: String = Watts.symbol + "/" + SquaredRadians.symbol
}

object RadiantIntensityConversions {
  lazy val wattPerSteradian: RadiantIntensity = WattsPerSteradian(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def wattsPerSteradian: RadiantIntensity = WattsPerSteradian(n)
  }

  given RadiantIntensityNumeric: AbstractQuantityNumeric[RadiantIntensity](RadiantIntensity.primaryUnit) {}
}

