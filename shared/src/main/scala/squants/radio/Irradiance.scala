/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2013-2015, Gary Keorkunian                                       **
**                                                                      **
\*                                                                      */

package squants.radio

import squants._
import squants.energy.{ ErgsPerSecond, Watts, WattHours }
import squants.space.{ SquareCentimeters, SquareMeters }
import squants.thermal.Kelvin
import squants.time.Hours
import scala.util.Try

/**
 * @author  garyKeorkunian
 * @since   0.1
 *
 * @param value Double
 */
final class Irradiance private (val value: Double, val unit: IrradianceUnit)
  extends Quantity[Irradiance] {

  def dimension = Irradiance

  def *(that: Area): Power = Watts(this.toWattsPerSquareMeter * that.toSquareMeters)
  /** The temperature of a blackbody that radiates this irradiance (Stefan-Boltzmann law): `T = (E / sigma)^(1/4)`. NaN if negative. */
  def blackbodyTemperature: Temperature = Kelvin(math.sqrt(math.sqrt(toWattsPerSquareMeter / PhysicalConstants.StefanBoltzmann)))
  // the Hours(1).toSeconds is to convert watt hours to watt seconds which
  // isn't a normal supported type in Squants
  def *(that: AreaTime): Energy = WattHours(
    this.toWattsPerSquareMeter * that.toSquareMeterSeconds / Hours(1).toSeconds)
  def /(that: Energy): ParticleFlux = BecquerelsPerSquareMeterSecond(
    toWattsPerSquareMeter / (that.toWattHours * Hours(1).toSeconds))
  def /(that: ParticleFlux): Energy = WattHours(
    (toWattsPerSquareMeter / that.toBecquerelsPerSquareMeterSecond) /
      Hours(1).toSeconds)

  def toWattsPerSquareMeter: Double = to(WattsPerSquareMeter)
  def toErgsPerSecondPerSquareCentimeter: Double = to(ErgsPerSecondPerSquareCentimeter)
}

object Irradiance extends Dimension[Irradiance] {
  private[radio] def apply[A](n: A, unit: IrradianceUnit)(using num: Numeric[A]) = new Irradiance(num.toDouble(n), unit)
  def apply(value: Any): Try[Irradiance] = parse(value)
  def name = "Irradiance"
  def primaryUnit = WattsPerSquareMeter
  def siUnit = WattsPerSquareMeter
  def units: Set[UnitOfMeasure[Irradiance]] = Set(WattsPerSquareMeter, ErgsPerSecondPerSquareCentimeter)
}

trait IrradianceUnit extends UnitOfMeasure[Irradiance] with UnitConverter {
  def apply[A](n: A)(using num: Numeric[A]): Irradiance = Irradiance(n, this)
}

object WattsPerSquareMeter extends IrradianceUnit with PrimaryUnit with SiUnit {
  val symbol: String = Watts.symbol + "/" + SquareMeters.symbol
}

object ErgsPerSecondPerSquareCentimeter extends IrradianceUnit {
  val conversionFactor: Double = ErgsPerSecond.conversionFactor / SquareCentimeters.conversionFactor
  val symbol: String = ErgsPerSecond.symbol + "/" + SquareCentimeters.symbol
}

object IrradianceConversions {
  lazy val wattPerSquareMeter: Irradiance = WattsPerSquareMeter(1)
  lazy val ergsPerSecondPerSquareCentimeter: Irradiance = ErgsPerSecondPerSquareCentimeter(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def wattsPerSquareMeter: Irradiance = WattsPerSquareMeter(n)
    def ergsPerSecondPerSquareCentimeter: Irradiance = ErgsPerSecondPerSquareCentimeter(n)
  }

  given IrradianceNumeric: AbstractQuantityNumeric[Irradiance](Irradiance.primaryUnit) {}
}
