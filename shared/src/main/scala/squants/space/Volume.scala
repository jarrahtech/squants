/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2013-2015, Gary Keorkunian                                       **
**                                                                      **
\*                                                                      */

package squants.space

import squants._
import squants.energy.{ EnergyDensity, Joules }
import squants.mass.{ ChemicalAmount, Kilograms }
import squants.motion.{ CubicMetersPerSecond, GravitationalParameter, VolumeFlow }
import squants.time.{ Seconds, TimeIntegral, TimeSquared }
import scala.util.Try

/**
 * Represents a quantity of Volume (three-dimensional space)
 *
 * @author  garyKeorkunian
 * @since   0.1
 *
 * @param value value in [[squants.space.CubicMeters]]
 */
final class Volume private (val value: Double, val unit: VolumeUnit)
  extends Quantity[Volume]
  with TimeIntegral[VolumeFlow] {

  def dimension = Volume

  protected def timeDerived: VolumeFlow = CubicMetersPerSecond(toCubicMeters)
  protected[squants] def time: Time = Seconds(1)

  def *(that: Density): Mass = Kilograms(this.toCubicMeters * that.toKilogramsPerCubicMeter)
  def *(that: EnergyDensity): Energy = Joules(this.toCubicMeters * that.toJoulesPerCubicMeter)

  def /(that: Area): Length = unit match {
    case CubicUsMiles => UsMiles(this.value / that.toSquareUsMiles)
    case CubicYards => Yards(this.value / that.toSquareYards)
    case CubicFeet => Feet(this.value / that.toSquareFeet)
    case CubicInches => Inches(this.value / that.toSquareInches)
    case _ => Meters(this.toCubicMeters / that.toSquareMeters)
  }

  def /(that: Length): Area = unit match {
    case CubicUsMiles => SquareUsMiles(this.value / that.toUsMiles)
    case CubicYards => SquareYards(this.value / that.toYards)
    case CubicFeet => SquareFeet(this.value / that.toFeet)
    case CubicInches => SquareInches(this.value / that.toInches)
    case _ => SquareMeters(this.toCubicMeters / that.toMeters)
  }

  def /(that: Mass) = ??? // returns SpecificVolume (inverse of Density)
  /** The period squared of an orbit whose semi-major axis cubed is this volume: `T² = a³ / mu`. */
  def /(that: GravitationalParameter): TimeSquared = Seconds(math.sqrt(this.toCubicMeters / that.toCubicMetersPerSecondSquared)).squared
  def /(that: ChemicalAmount) = ??? // return MolarVolume

  def cubeRoot: Length = Meters(math.cbrt(toCubicMeters))

  def toCubicMeters: Double = to(CubicMeters)
  def toLitres: Double = to(Litres)
  def toNanolitres: Double = to(Nanolitres)
  def toMicrolitres: Double = to(Microlitres)
  def toMillilitres: Double = to(Millilitres)
  def toCentilitres: Double = to(Centilitres)
  def toDecilitres: Double = to(Decilitres)
  def toHectolitres: Double = to(Hectolitres)

  def toCubicMiles: Double = to(CubicUsMiles)
  def toCubicYards: Double = to(CubicYards)
  def toCubicFeet: Double = to(CubicFeet)
  def toCubicInches: Double = to(CubicInches)

  def toUsGallons: Double = to(UsGallons)
  def toUsQuarts: Double = to(UsQuarts)
  def toUsPints: Double = to(UsPints)
  def toUsCups: Double = to(UsCups)
  def toFluidOunces: Double = to(FluidOunces)
  def toTablespoons: Double = to(Tablespoons)
  def toTeaspoons: Double = to(Teaspoons)

  def toUsDryGallons: Double = to(UsDryGallons)
  def toUsDryQuarts: Double = to(UsDryQuarts)
  def toUsDryPints: Double = to(UsDryPints)
  def toUsDryCups: Double = to(UsDryCups)

  def toImperialGallons: Double = to(ImperialGallons)
  def toImperialQuarts: Double = to(ImperialQuarts)
  def toImperialPints: Double = to(ImperialPints)
  def toImperialCups: Double = to(ImperialCups)

  def toAcreFeet: Double = to(AcreFeet)
}

object Volume extends Dimension[Volume] {
  private[space] def apply[A](n: A, unit: VolumeUnit)(using num: Numeric[A]) = new Volume(num.toDouble(n), unit)
  def apply(value: Any): Try[Volume] = parse(value)
  def name = "Volume"
  def primaryUnit = CubicMeters
  def siUnit = CubicMeters
  def units: Set[UnitOfMeasure[Volume]] = Set(CubicMeters, Litres, Nanolitres, Microlitres, Millilitres, Centilitres,
    Decilitres, Hectolitres,
    CubicUsMiles, CubicYards, CubicFeet, CubicInches,
    UsGallons, UsQuarts, UsPints, UsCups, FluidOunces, Tablespoons, Teaspoons,
    AcreFeet)
}

trait VolumeUnit extends UnitOfMeasure[Volume] with UnitConverter {
  def apply[A](n: A)(using num: Numeric[A]): Volume = Volume(n, this)
}

object CubicMeters extends VolumeUnit with PrimaryUnit with SiUnit {
  val symbol = "m³"
}

object Litres extends VolumeUnit {
  val symbol = "L"
  val conversionFactor = .001
}

object Nanolitres extends VolumeUnit {
  val symbol = "nl"
  val conversionFactor: Double = Litres.conversionFactor * MetricSystem.Nano
}

object Microlitres extends VolumeUnit {
  val symbol = "µl"
  val conversionFactor: Double = Litres.conversionFactor * MetricSystem.Micro
}

object Millilitres extends VolumeUnit {
  val symbol = "ml"
  val conversionFactor: Double = Litres.conversionFactor * MetricSystem.Milli
}

object Centilitres extends VolumeUnit {
  val symbol = "cl"
  val conversionFactor: Double = Litres.conversionFactor * MetricSystem.Centi
}

object Decilitres extends VolumeUnit {
  val symbol = "dl"
  val conversionFactor: Double = Litres.conversionFactor * MetricSystem.Deci
}

object Hectolitres extends VolumeUnit {
  val symbol = "hl"
  val conversionFactor: Double = Litres.conversionFactor * MetricSystem.Hecto
}

object CubicUsMiles extends VolumeUnit {
  val symbol = "mi³"
  val conversionFactor: Double = math.pow(UsMiles.conversionFactor, 3)
}

object CubicYards extends VolumeUnit {
  val symbol = "yd³"
  val conversionFactor: Double = BigDecimal(Yards.conversionFactor).pow(3).toDouble
}

object CubicFeet extends VolumeUnit {
  val symbol = "ft³"
  val conversionFactor: Double = BigDecimal(Feet.conversionFactor).pow(3).toDouble
}

object CubicInches extends VolumeUnit {
  val symbol = "in³"
  val conversionFactor: Double = math.pow(Inches.conversionFactor, 3)
}

object UsGallons extends VolumeUnit {
  val symbol = "gal"
  val conversionFactor: Double = Millilitres.conversionFactor * 3.785411784e3
}

object UsQuarts extends VolumeUnit {
  val symbol = "qt"
  val conversionFactor: Double = UsGallons.conversionFactor / 4d
}

object UsPints extends VolumeUnit {
  val symbol = "pt"
  val conversionFactor: Double = UsGallons.conversionFactor / 8d
}

object UsCups extends VolumeUnit {
  val symbol = "c"
  val conversionFactor: Double = UsGallons.conversionFactor / 16d
}

object FluidOunces extends VolumeUnit {
  val symbol = "oz"
  val conversionFactor: Double = UsGallons.conversionFactor / 128d
}

object Tablespoons extends VolumeUnit {
  val symbol = "tbsp"
  val conversionFactor: Double = FluidOunces.conversionFactor / 2d
}

object Teaspoons extends VolumeUnit {
  val symbol = "tsp"
  val conversionFactor: Double = FluidOunces.conversionFactor / 6d
}

object UsDryGallons extends VolumeUnit {
  val symbol = "gal"
  val conversionFactor: Double = Millilitres.conversionFactor * 4.4048837e3
}

object UsDryQuarts extends VolumeUnit {
  val symbol = "qt"
  val conversionFactor: Double = UsDryGallons.conversionFactor / 4d
}

object UsDryPints extends VolumeUnit {
  val symbol = "pt"
  val conversionFactor: Double = UsDryGallons.conversionFactor / 8d
}

object UsDryCups extends VolumeUnit {
  val symbol = "c"
  val conversionFactor: Double = UsDryGallons.conversionFactor / 16d
}

object ImperialGallons extends VolumeUnit {
  val symbol = "gal"
  val conversionFactor: Double = Millilitres.conversionFactor * 4.54609e3
}

object ImperialQuarts extends VolumeUnit {
  val symbol = "qt"
  val conversionFactor: Double = ImperialGallons.conversionFactor / 4d
}

object ImperialPints extends VolumeUnit {
  val symbol = "pt"
  val conversionFactor: Double = ImperialGallons.conversionFactor / 8d
}

object ImperialCups extends VolumeUnit {
  val symbol = "c"
  val conversionFactor: Double = ImperialGallons.conversionFactor / 16d
}

object AcreFeet extends VolumeUnit {
  val symbol = "acft"
  val conversionFactor: Double = CubicFeet.conversionFactor * 43560d
}

object VolumeConversions {
  lazy val cubicMeter: Volume = CubicMeters(1)
  lazy val litre: Volume = Litres(1)
  lazy val liter: Volume = Litres(1)
  lazy val nanolitre: Volume = Nanolitres(1)
  lazy val nanoliter: Volume = Nanolitres(1)
  lazy val microlitre: Volume = Microlitres(1)
  lazy val microliter: Volume = Microlitres(1)
  lazy val millilitre: Volume = Millilitres(1)
  lazy val milliliter: Volume = Millilitres(1)
  lazy val centilitre: Volume = Centilitres(1)
  lazy val centiliter: Volume = Centilitres(1)
  lazy val decilitre: Volume = Decilitres(1)
  lazy val deciliter: Volume = Decilitres(1)
  lazy val hectolitre: Volume = Hectolitres(1)
  lazy val hectoliter: Volume = Hectolitres(1)

  lazy val cubicMile: Volume = CubicUsMiles(1)
  lazy val cubicYard: Volume = CubicYards(1)
  lazy val cubicFoot: Volume = CubicFeet(1)
  lazy val cubicInch: Volume = CubicInches(1)

  lazy val gallon: Volume = UsGallons(1)
  lazy val quart: Volume = UsQuarts(1)
  lazy val pint: Volume = UsPints(1)
  lazy val cup: Volume = UsCups(1)

  lazy val fluidOunce: Volume = FluidOunces(1)
  lazy val tablespoon: Volume = Tablespoons(1)
  lazy val teaspoon: Volume = Teaspoons(1)

  lazy val acreFoot: Volume = AcreFeet(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def cubicMeters: Volume = CubicMeters(n)
    def cubicMetres: Volume = CubicMeters(n)
    def litres: Volume = Litres(n)
    def liters: Volume = Litres(n)
    def nanolitres: Volume = Nanolitres(n)
    def nanoliters: Volume = Nanolitres(n)
    def microlitres: Volume = Microlitres(n)
    def microliters: Volume = Microlitres(n)
    def millilitres: Volume = Millilitres(n)
    def milliliters: Volume = Millilitres(n)
    def centilitres: Volume = Centilitres(n)
    def centiliters: Volume = Centilitres(n)
    def decilitres: Volume = Decilitres(n)
    def deciliters: Volume = Decilitres(n)
    def hectolitres: Volume = Hectolitres(n)
    def hectoliters: Volume = Hectolitres(n)

    def cubicMiles: Volume = CubicUsMiles(n)
    def cubicYards: Volume = CubicYards(n)
    def cubicFeet: Volume = CubicFeet(n)
    def cubicInches: Volume = CubicInches(n)

    def gallons: Volume = UsGallons(n)
    def quarts: Volume = UsQuarts(n)
    def pints: Volume = UsPints(n)
    def cups: Volume = UsCups(n)
    def fluidOunces: Volume = FluidOunces(n)
    def tablespoons: Volume = Tablespoons(n)
    def teaspoons: Volume = Teaspoons(n)

    def acreFeet: Volume = AcreFeet(n)
  }

  given VolumeNumeric: AbstractQuantityNumeric[Volume](Volume.primaryUnit) {}
}
