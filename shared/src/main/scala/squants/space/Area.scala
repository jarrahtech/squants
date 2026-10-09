/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2013-2015, Gary Keorkunian                                       **
**                                                                      **
\*                                                                      */

package squants.space

import squants._
import squants.electro.{ MagneticFlux, MagneticFluxDensity, Webers }
import squants.energy.Watts
import squants.mass.AreaDensity
import squants.motion.{ Newtons, Pressure }
import squants.photo.{ Candelas, _ }
import squants.radio._
import squants.time.Time
import scala.util.Try

/**
 * @author  garyKeorkunian
 * @since   0.1
 *
 * @param value value in [[squants.space.SquareMeters]]
 */
final class Area private (val value: Double, val unit: AreaUnit)
  extends Quantity[Area] {

  def dimension = Area

  def *(that: Length): Volume = unit match {
    case SquareUsMiles => CubicUsMiles(this.value * that.toUsMiles)
    case SquareYards => CubicYards(this.value * that.toYards)
    case SquareFeet => CubicFeet(this.value * that.toFeet)
    case SquareInches => CubicInches(this.value * that.toInches)
    case _ => CubicMeters(this.toSquareMeters * that.toMeters)
  }

  def *(that: AreaDensity): Mass = Kilograms(this.toSquareMeters * that.toKilogramsPerSquareMeter)
  def *(that: Pressure): Force = Newtons(this.toSquareMeters * that.toPascals)
  def *(that: Illuminance): LuminousFlux = Lumens(this.toSquareMeters * that.toLux)
  def *(that: Luminance): LuminousIntensity = Candelas(this.toSquareMeters * that.toCandelasPerSquareMeters)
  def *(that: MagneticFluxDensity): MagneticFlux = Webers(this.toSquareMeters * that.toTeslas)
  def *(that: Irradiance): Power = Watts(this.toSquareMeters * that.toWattsPerSquareMeter)
  def *(that: Radiance): RadiantIntensity = WattsPerSteradian(this.toSquareMeters * that.toWattsPerSteradianPerSquareMeter)
  def *(that: Time): AreaTime = SquareMeterSeconds(this.toSquareMeters * that.toSeconds)

  def /(that: Length): Length = unit match {
    case SquareUsMiles => UsMiles(this.value / that.toUsMiles)
    case SquareYards => Yards(this.value / that.toYards)
    case SquareFeet => Feet(this.value / that.toFeet)
    case SquareInches => Inches(this.value / that.toInches)
    case _ => Meters(this.toSquareMeters / that.toMeters)
  }

  def squareRoot: Length = Meters(math.sqrt(toSquareMeters))

  def toSquareMeters: Double = to(SquareMeters)
  def toSquareCentimeters: Double = to(SquareCentimeters)
  def toSquareKilometers: Double = to(SquareKilometers)
  def toSquareUsMiles: Double = to(SquareUsMiles)
  def toSquareYards: Double = to(SquareYards)
  def toSquareFeet: Double = to(SquareFeet)
  def toSquareInches: Double = to(SquareInches)
  def toHectares: Double = to(Hectares)
  def toAcres: Double = to(Acres)
  def toBarnes: Double = to(Barnes)
}

object Area extends Dimension[Area] {
  private[space] def apply[A](n: A, unit: AreaUnit)(using num: Numeric[A]) = new Area(num.toDouble(n), unit)
  def apply(value: Any): Try[Area] = parse(value)
  def name = "Area"
  def primaryUnit = SquareMeters
  def siUnit = SquareMeters
  def units: Set[UnitOfMeasure[Area]] = Set(SquareMeters, SquareCentimeters, SquareKilometers,
    SquareUsMiles, SquareYards, SquareFeet, SquareInches,
    Hectares, Acres, Barnes)
}

trait AreaUnit extends UnitOfMeasure[Area] with UnitConverter {
  def apply[A](n: A)(using num: Numeric[A]): Area = Area(n, this)
}

object SquareMeters extends AreaUnit with PrimaryUnit with SiUnit {
  val symbol = "m²"
}

object SquareCentimeters extends AreaUnit with SiUnit {
  val symbol = "cm²"
  val conversionFactor: Double = MetricSystem.Centi * MetricSystem.Centi
}

object SquareKilometers extends AreaUnit with SiUnit {
  val symbol = "km²"
  val conversionFactor: Double = MetricSystem.Kilo * MetricSystem.Kilo
}

object SquareUsMiles extends AreaUnit {
  val symbol = "mi²"
  val conversionFactor: Double = 2.589988110336 * SquareKilometers.conversionFactor
}

object SquareYards extends AreaUnit {
  val symbol = "yd²"
  val conversionFactor = 8.3612736e-1
}

object SquareFeet extends AreaUnit {
  val symbol = "ft²"
  val conversionFactor = 9.290304e-2
}

object SquareInches extends AreaUnit {
  val symbol = "in²"
  val conversionFactor: Double = 6.4516 * SquareCentimeters.conversionFactor
}

object Hectares extends AreaUnit {
  val symbol = "ha"
  val conversionFactor = 10000d
}

object Acres extends AreaUnit {
  val symbol = "acre"
  val conversionFactor: Double = 43560d * SquareFeet.conversionFactor
}

object Barnes extends AreaUnit {
  val symbol = "b"
  val conversionFactor: Double = scala.math.pow(10, -28)
}

object AreaConversions {
  lazy val squareMeter: Area = SquareMeters(1)
  lazy val squareCentimeter: Area = SquareCentimeters(1)
  lazy val squareKilometer: Area = SquareKilometers(1)
  lazy val squareMile: Area = SquareUsMiles(1)
  lazy val squareYard: Area = SquareYards(1)
  lazy val squareFoot: Area = SquareFeet(1)
  lazy val squareInch: Area = SquareInches(1)
  lazy val hectare: Area = Hectares(1)
  lazy val acre: Area = Acres(1)
  lazy val barne: Area = Barnes(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def squareMeters: Area = SquareMeters(n)
    def squareCentimeters: Area = SquareCentimeters(n)
    def squareKilometers: Area = SquareKilometers(n)
    def squareMiles: Area = SquareUsMiles(n)
    def squareYards: Area = SquareYards(n)
    def squareFeet: Area = SquareFeet(n)
    def squareInches: Area = SquareInches(n)
    def hectares: Area = Hectares(n)
    def acres: Area = Acres(n)
    def barnes: Area = Barnes(n)
  }

  given AreaNumeric: AbstractQuantityNumeric[Area](Area.primaryUnit) {}
}
