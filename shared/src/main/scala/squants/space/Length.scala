/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2013-2015, Gary Keorkunian                                       **
**                                                                      **
\*                                                                      */

package squants.space

import squants._
import squants.electro._
import squants.energy.{ Joules, Watts }
import squants.motion.{ MetersPerSecond, Velocity }
import squants.radio.{ RadiantIntensity, SpectralIntensity, SpectralPower, WattsPerSteradian }
import squants.time.{ SecondTimeIntegral, TimeIntegral, TimeSquared }
import scala.util.Try

/**
 * Represents a quantity of length
 *
 * @author  garyKeorkunian
 * @since   0.1
 *
 * @param value value in  [[squants.space.Meters]]
 */
final class Length private (val value: Double, val unit: LengthUnit)
  extends Quantity[Length]
  with TimeIntegral[Velocity]
  with SecondTimeIntegral[Acceleration] {

  def dimension = Length

  protected def timeDerived: Velocity = MetersPerSecond(toMeters)
  protected[squants] def time: Time = Seconds(1)

  def *(that: Length): Area = unit match {
    case Centimeters => SquareCentimeters(this.value * that.toCentimeters)
    case Kilometers => SquareKilometers(this.value * that.toKilometers)
    case UsMiles => SquareUsMiles(this.value * that.toUsMiles)
    case Yards => SquareYards(this.value * that.toYards)
    case Feet => SquareFeet(this.value * that.toFeet)
    case Inches => SquareInches(this.value * that.toInches)
    case _ => SquareMeters(toMeters * that.toMeters)
  }

  def *(that: Area): Volume = unit match {
    case Yards => CubicYards(this.value * that.toSquareYards)
    case Feet => CubicFeet(this.value * that.toSquareFeet)
    case Inches => CubicInches(this.value * that.toSquareInches)
    case _ => CubicMeters(this.toMeters * that.toSquareMeters)
  }

  def *(that: Force): Energy = Joules(this.toMeters * that.toNewtons)
  def *(that: SpectralIntensity): RadiantIntensity = WattsPerSteradian(this.toMeters * that.toWattsPerSteradianPerMeter)
  def *(that: SpectralPower): Power = Watts(this.toMeters * that.toWattsPerMeter)
  def *(that: Conductivity): ElectricalConductance = Siemens(this.toMeters * that.toSiemensPerMeter)
  def *(that: ElectricalResistance): Resistivity = OhmMeters(this.toMeters * that.toOhms)

  def /(that: TimeSquared): Acceleration = this / that.time1 / that.time2
  def /(that: Acceleration): TimeSquared = (this / that.timeIntegrated) * time

  def squared: Area = this * this
  def cubed: Volume = this * this * this

  def toAngstroms: Double = to(Angstroms)
  def toNanometers: Double = to(Nanometers)
  def toMicrons: Double = to(Microns)
  def toMillimeters: Double = to(Millimeters)
  def toCentimeters: Double = to(Centimeters)
  def toDecimeters: Double = to(Decimeters)
  def toMeters: Double = to(Meters)
  def toDecameters: Double = to(Decameters)
  def toHectometers: Double = to(Hectometers)
  def toKilometers: Double = to(Kilometers)
  def toInches: Double = to(Inches)
  def toFeet: Double = to(Feet)
  def toYards: Double = to(Yards)
  def toUsMiles: Double = to(UsMiles)
  def toInternationalMiles: Double = to(InternationalMiles)
  def toNauticalMiles: Double = to(NauticalMiles)
  def toAstronomicalUnits: Double = to(AstronomicalUnits)
  def toLightYears: Double = to(LightYears)
  def toParsecs: Double = to(Parsecs)
  def toKiloParsecs: Double = to(KiloParsecs)
  def toMegaParsecs: Double = to(MegaParsecs)
  def toGigaParsecs: Double = to(GigaParsecs)
  def toSolarRadii: Double = to(SolarRadii)
  def toNominalSolarRadii: Double = to(NominalSolarRadii)
  def toEarthRadii: Double = to(EarthRadii)
  def toeV: Double = to(ElectronVoltLength)
  def tomeV: Double = to(MilliElectronVoltLength)
  def tokeV: Double = to(KiloElectronVoltLength)
  def toMeV: Double = to(MegaElectronVoltLength)
  def toGeV: Double = to(GigaElectronVoltLength)
  def toTeV: Double = to(TeraElectronVoltLength)
  def toPeV: Double = to(PetaElectronVoltLength)
  def toEeV: Double = to(ExaElectronVoltLength)

}

/**
 * Factory singleton for length
 */
object Length extends Dimension[Length] with BaseDimension {
  private[space] def apply[A](n: A, unit: LengthUnit)(using num: Numeric[A]) = new Length(num.toDouble(n), unit)
  def apply(value: Any): Try[Length] = parse(value)
  def name = "Length"
  def primaryUnit = Meters
  def siUnit = Meters
  def units: Set[UnitOfMeasure[Length]] = Set(Angstroms, Nanometers, Microns, Millimeters, Centimeters,
    Decimeters, Meters, Decameters, Hectometers, Kilometers,
    Inches, Feet, Yards, UsMiles, InternationalMiles, NauticalMiles,
    AstronomicalUnits, LightYears, Parsecs, KiloParsecs, MegaParsecs, GigaParsecs, SolarRadii, NominalSolarRadii, EarthRadii,
    ElectronVoltLength, MilliElectronVoltLength, KiloElectronVoltLength, MegaElectronVoltLength,
    GigaElectronVoltLength, TeraElectronVoltLength, PetaElectronVoltLength, ExaElectronVoltLength)
  def dimensionSymbol = "L"
}

/**
 * Base trait for units of [[squants.space.Length]]
 */
trait LengthUnit extends UnitOfMeasure[Length] with UnitConverter {
  def apply[A](n: A)(using num: Numeric[A]): Length = Length(n, this)
}

object Angstroms extends LengthUnit {
  // note: the symbol used here is the letter "\u00C5" which is to be preferred over the angstrom sign "\u212B"
  // see also: https://en.wikipedia.org/wiki/Å and http://www.fileformat.info/info/unicode/char/00c5/index.htm
  val symbol = "Å"
  val conversionFactor: Double = 100 * MetricSystem.Pico
}

object Nanometers extends LengthUnit with SiUnit {
  val symbol = "nm"
  val conversionFactor = MetricSystem.Nano
}

object Microns extends LengthUnit with SiUnit {
  val symbol = "µm"
  val conversionFactor = MetricSystem.Micro
}

object Millimeters extends LengthUnit with SiUnit {
  val symbol = "mm"
  val conversionFactor = MetricSystem.Milli
}

object Centimeters extends LengthUnit with SiUnit {
  val symbol = "cm"
  val conversionFactor = MetricSystem.Centi
}

object Decimeters extends LengthUnit with SiUnit {
  val symbol = "dm"
  val conversionFactor = MetricSystem.Deci
}

object Meters extends LengthUnit with PrimaryUnit with SiBaseUnit {
  val symbol = "m"
}

object Decameters extends LengthUnit with SiUnit {
  val symbol = "dam"
  val conversionFactor = MetricSystem.Deca
}

object Hectometers extends LengthUnit with SiUnit {
  val symbol = "hm"
  val conversionFactor = MetricSystem.Hecto
}

object Kilometers extends LengthUnit with SiUnit {
  val symbol = "km"
  val conversionFactor = MetricSystem.Kilo
}

object Inches extends LengthUnit {
  val conversionFactor: Double = Feet.conversionFactor / 12d
  val symbol = "in"
}

object Feet extends LengthUnit {
  val conversionFactor = 3.048006096e-1
  val symbol = "ft"
}

object Yards extends LengthUnit {
  val conversionFactor: Double = Feet.conversionFactor * 3d
  val symbol = "yd"
}

object UsMiles extends LengthUnit {
  val conversionFactor: Double = Feet.conversionFactor * 5.28e3
  val symbol = "mi"
}

object InternationalMiles extends LengthUnit {
  val conversionFactor = 1.609344e3
  val symbol = "mile"
}

object NauticalMiles extends LengthUnit {
  val conversionFactor = 1.852e3
  val symbol = "nmi"
}

object AstronomicalUnits extends LengthUnit {
  val conversionFactor = 1.495978707e11
  val symbol = "au"
}

object LightYears extends LengthUnit {
  val conversionFactor = 9.4607304725808e15
  val symbol = "ly"
}

object Parsecs extends LengthUnit {
  val conversionFactor = 3.08567758149137e16
  val symbol = "pc"
}

object KiloParsecs extends LengthUnit {
  val conversionFactor: Double = Parsecs.conversionFactor * MetricSystem.Kilo
  val symbol = "kpc"
}

object MegaParsecs extends LengthUnit {
  val conversionFactor: Double = Parsecs.conversionFactor * MetricSystem.Mega
  val symbol = "Mpc"
}

object GigaParsecs extends LengthUnit {
  val conversionFactor: Double = Parsecs.conversionFactor * MetricSystem.Giga
  val symbol = "Gpc"
}

object SolarRadii extends LengthUnit {
  val conversionFactor = 6.957e8
  val symbol = "R☉"
}

object NominalSolarRadii extends LengthUnit {
  val conversionFactor = 6.957e8
  val symbol = "RN☉"
}

object EarthRadii extends LengthUnit {
  val conversionFactor: Double = 6371 * Kilometers.conversionFactor
  val symbol = "R🜨"
}

object ElectronVoltLength extends LengthUnit {
  val conversionFactor = 1.97327e-7
  val symbol = "ħc/eV"
}

object MilliElectronVoltLength extends LengthUnit {
  val conversionFactor: Double = ElectronVoltLength.conversionFactor * MetricSystem.Milli
  val symbol = "mħc/eV"
}

object KiloElectronVoltLength extends LengthUnit {
  val conversionFactor: Double = ElectronVoltLength.conversionFactor * MetricSystem.Kilo
  val symbol = "kħc/eV"
}

object MegaElectronVoltLength extends LengthUnit {
  val conversionFactor: Double = ElectronVoltLength.conversionFactor * MetricSystem.Mega
  val symbol = "Mħc/eV"
}

object GigaElectronVoltLength extends LengthUnit {
  val conversionFactor: Double = ElectronVoltLength.conversionFactor * MetricSystem.Giga
  val symbol = "Għc/eV"
}

object TeraElectronVoltLength extends LengthUnit {
  val conversionFactor: Double = ElectronVoltLength.conversionFactor * MetricSystem.Tera
  val symbol = "Tħc/eV"
}

object PetaElectronVoltLength extends LengthUnit {
  val conversionFactor: Double = ElectronVoltLength.conversionFactor * MetricSystem.Peta
  val symbol = "Pħc/eV"
}

object ExaElectronVoltLength extends LengthUnit {
  val conversionFactor: Double = ElectronVoltLength.conversionFactor * MetricSystem.Exa
  val symbol = "Eħc/eV"
}

object LengthConversions {
  lazy val angstrom: Length = Angstroms(1)
  lazy val nanometer: Length = Nanometers(1)
  lazy val nanometre: Length = Nanometers(1)
  lazy val micron: Length = Microns(1)
  lazy val micrometer: Length = Microns(1)
  lazy val micrometre: Length = Microns(1)
  lazy val millimeter: Length = Millimeters(1)
  lazy val millimetre: Length = Millimeters(1)
  lazy val centimeter: Length = Centimeters(1)
  lazy val centimetre: Length = Centimeters(1)
  lazy val decimeter: Length = Decimeters(1)
  lazy val decimetre: Length = Decimeters(1)
  lazy val meter: Length = Meters(1)
  lazy val metre: Length = Meters(1)
  lazy val decameter: Length = Decameters(1)
  lazy val decametre: Length = Decameters(1)
  lazy val hectometer: Length = Hectometers(1)
  lazy val hectometre: Length = Hectometers(1)
  lazy val kilometer: Length = Kilometers(1)
  lazy val kilometre: Length = Kilometers(1)
  lazy val inch: Length = Inches(1)
  lazy val foot: Length = Feet(1)
  lazy val yard: Length = Yards(1)
  lazy val mile: Length = UsMiles(1)
  lazy val nauticalMile: Length = NauticalMiles(1)
  lazy val astronomicalUnit: Length = AstronomicalUnits(1)
  lazy val lightYear: Length = LightYears(1)
  lazy val parsec: Length = Parsecs(1)
  lazy val kiloparsec: Length = KiloParsecs(1)
  lazy val megaparsec: Length = MegaParsecs(1)
  lazy val gigaparsec: Length = GigaParsecs(1)
  lazy val solarRadius: Length = SolarRadii(1)
  lazy val nominalSolarRadius: Length = NominalSolarRadii(1)

  lazy val eV: Length = ElectronVoltLength(1)
  lazy val meV: Length = MilliElectronVoltLength(1)
  lazy val keV: Length = KiloElectronVoltLength(1)
  lazy val MeV: Length = MegaElectronVoltLength(1)
  lazy val GeV: Length = GigaElectronVoltLength(1)
  lazy val TeV: Length = TeraElectronVoltLength(1)
  lazy val PeV: Length = PetaElectronVoltLength(1)
  lazy val EeV: Length = ExaElectronVoltLength(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def Å: Length = Angstroms(n)
    def angstroms: Length = Angstroms(n)
    def nm: Length = Nanometers(n)
    def nanometers: Length = Nanometers(n)
    def nanometres: Length = Nanometers(n)
    def µm: Length = Microns(n)
    def microns: Length = Microns(n)
    def micrometer: Length = Microns(n)
    def micrometre: Length = Microns(n)
    def mm: Length = Millimeters(n)
    def millimeters: Length = Millimeters(n)
    def millimetres: Length = Millimeters(n)
    def cm: Length = Centimeters(n)
    def centimeters: Length = Centimeters(n)
    def centimetres: Length = Centimeters(n)
    def dm: Length = Decimeters(n)
    def meters: Length = Meters(n)
    def metres: Length = Meters(n)
    def dam: Length = Decameters(n)
    def hm: Length = Hectometers(n)
    def km: Length = Kilometers(n)
    def kilometers: Length = Kilometers(n)
    def kilometres: Length = Kilometers(n)
    def inches: Length = Inches(n)
    def ft: Length = Feet(n)
    def feet: Length = Feet(n)
    def yd: Length = Yards(n)
    def yards: Length = Yards(n)
    def miles: Length = UsMiles(n)
    def nmi: Length = NauticalMiles(n)
    def au: Length = AstronomicalUnits(n)
    def ly: Length = LightYears(n)
    def lightYears: Length = LightYears(n)
    def parsecs: Length = Parsecs(n)
    def pc: Length = Parsecs(n)
    def kpc: Length = KiloParsecs(n)
    def Mpc: Length = MegaParsecs(n)
    def Gpc: Length = GigaParsecs(n)
    def solarRadii: Length = SolarRadii(n)
    def nominalSolarRadii: Length = NominalSolarRadii(n)
    def eV: Length = ElectronVoltLength(n)
    def meV: Length = MilliElectronVoltLength(n)
    def keV: Length = KiloElectronVoltLength(n)
    def MeV: Length = MegaElectronVoltLength(n)
    def GeV: Length = GigaElectronVoltLength(n)
    def TeV: Length = TeraElectronVoltLength(n)
    def PeV: Length = PetaElectronVoltLength(n)
    def EeV: Length = ExaElectronVoltLength(n)
  }

  extension (s: String) {
    def toLength: Try[Length] = Length(s)
  }

  given LengthNumeric: AbstractQuantityNumeric[Length](Length.primaryUnit) {}
}

