/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2013-2015, Gary Keorkunian                                       **
**                                                                      **
\*                                                                      */

package squants.radio

import squants._
import squants.energy.{ ErgsPerSecond, Watts }
import squants.space._
import scala.util.Try

/**
 * @author  florianNussberger
 * @since   0.6
 *
 * @param value Double
 */
final class SpectralIrradiance private (val value: Double, val unit: SpectralIrradianceUnit)
  extends Quantity[SpectralIrradiance] {

  def dimension = SpectralIrradiance

  def toWattsPerCubicMeter: Double = to(WattsPerCubicMeter)
  def toWattsPerSquareMeterPerNanometer: Double = to(WattsPerSquareMeterPerNanometer)
  def toWattsPerSquareMeterPerMicron: Double = to(WattsPerSquareMeterPerMicron)
  def toErgsPerSecondPerSquareCentimeterPerAngstrom: Double = to(ErgsPerSecondPerSquareCentimeterPerAngstrom)
}

object SpectralIrradiance extends Dimension[SpectralIrradiance] {
  private[radio] def apply[A](n: A, unit: SpectralIrradianceUnit)(using num: Numeric[A]) = new SpectralIrradiance(num.toDouble(n), unit)
  def apply(value: Any): Try[SpectralIrradiance] = parse(value)
  def name = "SpectralIrradiance"
  def primaryUnit = WattsPerCubicMeter
  def siUnit = WattsPerCubicMeter
  def units: Set[UnitOfMeasure[SpectralIrradiance]] = Set(WattsPerCubicMeter, WattsPerSquareMeterPerMicron, WattsPerSquareMeterPerNanometer, ErgsPerSecondPerSquareCentimeterPerAngstrom)
}

trait SpectralIrradianceUnit extends UnitOfMeasure[SpectralIrradiance] with UnitConverter {
  def apply[A](n: A)(using num: Numeric[A]): SpectralIrradiance = SpectralIrradiance(n, this)
}

object WattsPerCubicMeter extends SpectralIrradianceUnit with PrimaryUnit with SiUnit {
  val symbol: String = Watts.symbol + "/" + CubicMeters.symbol
}

object WattsPerSquareMeterPerNanometer extends SpectralIrradianceUnit with SiUnit {
  val conversionFactor: Double = 1 / MetricSystem.Nano
  val symbol: String = Watts.symbol + "/" + SquareMeters.symbol + "/" + Nanometers.symbol
}

object WattsPerSquareMeterPerMicron extends SpectralIrradianceUnit with SiUnit {
  val conversionFactor: Double = 1 / MetricSystem.Micro
  val symbol: String = Watts.symbol + "/" + SquareMeters.symbol + "/" + Microns.symbol
}

object ErgsPerSecondPerSquareCentimeterPerAngstrom extends SpectralIrradianceUnit {
  val conversionFactor: Double = ErgsPerSecond.conversionFactor / SquareCentimeters.conversionFactor / Angstroms.conversionFactor
  val symbol: String = ErgsPerSecond.symbol + "/" + SquareCentimeters.symbol + "/" + Angstroms.symbol
}

object SpectralIrradianceConversions {
  lazy val wattPerCubicMeter: SpectralIrradiance = WattsPerCubicMeter(1)
  lazy val wattPerSquareMeterPerNanometer: SpectralIrradiance = WattsPerSquareMeterPerNanometer(1)
  lazy val wattPerSquareMeterPerMicron: SpectralIrradiance = WattsPerSquareMeterPerMicron(1)
  lazy val ergPerSecondPerSquareCentimeterPerAngstrom: SpectralIrradiance = ErgsPerSecondPerSquareCentimeterPerAngstrom(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def wattsPerCubicMeter: SpectralIrradiance = WattsPerCubicMeter(n)
    def wattsPerSquareMeterPerNanometer: SpectralIrradiance = WattsPerSquareMeterPerNanometer(n)
    def wattsPerSquareMeterPerMicron: SpectralIrradiance = WattsPerSquareMeterPerMicron(n)
    def ergsPerSecondPerSquareCentimeterPerAngstrom: SpectralIrradiance = ErgsPerSecondPerSquareCentimeterPerAngstrom(n)
  }

  given SpectralIrradianceNumeric: AbstractQuantityNumeric[SpectralIrradiance](SpectralIrradiance.primaryUnit) {}
}
