/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2013-2015, Gary Keorkunian                                       **
**                                                                      **
\*                                                                      */

package squants.mass

import squants.energy.{ Energy, Joules, SpecificEnergy }
import squants.motion.{ Force, MassFlow, Momentum, _ }
import squants.space.{ CubicMeters, SquareMeters }
import squants.time.TimeIntegral
import squants.{ Acceleration, Energy => _, Velocity, _ }
import scala.util.Try

/**
 * Represents a quantity of Mass
 *
 * @author  garyKeorkunian
 * @since   0.1
 *
 * @param value the value in the [[squants.mass.Grams]]
 */
final class Mass private (val value: Double, val unit: MassUnit)
  extends Quantity[Mass]
  with TimeIntegral[MassFlow] {

  def dimension = Mass

  protected def timeDerived: MassFlow = KilogramsPerSecond(toKilograms)
  protected def time: Time = Seconds(1)

  def *(that: SpecificEnergy): Energy = Joules(this.toKilograms * that.toGrays)
  def *(that: Velocity): Momentum = Momentum(this, that)
  def *(that: Acceleration): Force = Newtons(this.toKilograms * that.toMetersPerSecondSquared)
  def /(that: Density): Volume = CubicMeters(this.toKilograms / that.toKilogramsPerCubicMeter)
  def /(that: Volume): Density = Density(this, that)
  def /(that: AreaDensity): Area = SquareMeters(this.toKilograms / that.toKilogramsPerSquareMeter)
  def /(that: Area): AreaDensity = KilogramsPerSquareMeter(this.toKilograms / that.toSquareMeters)

  /**
   * Moment of inertia of a point mass with with this mass and the given
   * radius from the center of rotation
   * @param radius length to center of rotation
   * @return moment of inertia of a point mass with given mass and radius
   */
  infix def onRadius(radius: Length): MomentOfInertia = KilogramsMetersSquared(toKilograms * radius.squared.toSquareMeters)

  def toNanograms: Double = to(Nanograms)
  def toMicrograms: Double = to(Micrograms)
  def toMilligrams: Double = to(Milligrams)
  def toGrams: Double = to(Grams)
  def toKilograms: Double = to(Kilograms)
  def toTonnes: Double = to(Tonnes)
  def toOunces: Double = to(Ounces)
  def toPounds: Double = to(Pounds)
  def toKilopounds: Double = to(Kilopounds)
  def toMegapounds: Double = to(Megapounds)
  def toStone: Double = to(Stone)
  def toTroyGrains: Double = to(TroyGrains)
  def toPennyweights: Double = to(Pennyweights)
  def toTroyOunces: Double = to(TroyOunces)
  def toTroyPounds: Double = to(TroyPounds)
  def toTolas: Double = to(Tolas)
  def toCarats: Double = to(Carats)
  def toSolarMasses: Double = to(SolarMasses)
  def toEarthMasses: Double = to(EarthMasses)
  def toDalton: Double = to(Dalton)

  def toeV: Double = to(ElectronVoltMass)
  def tomeV: Double = to(MilliElectronVoltMass)
  def tokeV: Double = to(KiloElectronVoltMass)
  def toMeV: Double = to(MegaElectronVoltMass)
  def toGeV: Double = to(GigaElectronVoltMass)
  def toTeV: Double = to(TeraElectronVoltMass)
  def toPeV: Double = to(PetaElectronVoltMass)
  def toEeV: Double = to(ExaElectronVoltMass)
}

/**
 * Factory singleton for [[squants.mass.Mass]] values
 */
object Mass extends Dimension[Mass] with BaseDimension {
  private[mass] def apply[A](n: A, unit: MassUnit)(using num: Numeric[A]) = new Mass(num.toDouble(n), unit)
  def apply(value: Any): Try[Mass] = parse(value)
  def name = "Mass"
  def primaryUnit = Grams
  def siUnit = Kilograms
  def units: Set[UnitOfMeasure[Mass]] = Set(Nanograms, Micrograms, Milligrams, Grams, Kilograms, Tonnes, Ounces, Pounds, Kilopounds, Megapounds,
    Stone, TroyGrains, Pennyweights, TroyOunces, TroyPounds, Tolas, Carats, SolarMasses, EarthMasses, Dalton,
    ElectronVoltMass, MilliElectronVoltMass, KiloElectronVoltMass, MegaElectronVoltMass,
    GigaElectronVoltMass, TeraElectronVoltMass, PetaElectronVoltMass, ExaElectronVoltMass)
  def dimensionSymbol = "M"
}

/**
 * Base trait for units of [[squants.mass.Mass]]
 */
trait MassUnit extends UnitOfMeasure[Mass] with UnitConverter {
  def apply[A](n: A)(using num: Numeric[A]): Mass = Mass(n, this)
}

object Grams extends MassUnit with PrimaryUnit with SiUnit {
  val symbol = "g"
}

object Nanograms extends MassUnit with SiUnit {
  val conversionFactor = MetricSystem.Nano
  val symbol = "ng"
}

object Micrograms extends MassUnit with SiUnit {
  val conversionFactor = MetricSystem.Micro
  val symbol = "mcg"
}

object Milligrams extends MassUnit with SiUnit {
  val conversionFactor = MetricSystem.Milli
  val symbol = "mg"
}

object Kilograms extends MassUnit with SiBaseUnit {
  val conversionFactor = MetricSystem.Kilo
  val symbol = "kg"
}

object Tonnes extends MassUnit {
  val conversionFactor = MetricSystem.Mega
  val symbol = "t"
}

object Ounces extends MassUnit {
  val conversionFactor: Double = Pounds.conversionFactor / 16d
  val symbol = "oz"
}

object Pounds extends MassUnit {
  val conversionFactor: Double = Kilograms.conversionFactor * 4.5359237e-1
  val symbol = "lb"
}

object Kilopounds extends MassUnit {
  val conversionFactor: Double = Pounds.conversionFactor * MetricSystem.Kilo
  val symbol = "klb"
}

object Megapounds extends MassUnit {
  val conversionFactor: Double = Pounds.conversionFactor * MetricSystem.Mega
  val symbol = "Mlb"
}

object Stone extends MassUnit {
  val conversionFactor: Double = Pounds.conversionFactor * 14d
  val symbol = "st"
}

object TroyGrains extends MassUnit {
  val conversionFactor: Double = 64.79891 * Milligrams.conversionFactor
  val symbol = "gr"
}

object Pennyweights extends MassUnit {
  val conversionFactor: Double = 24d * TroyGrains.conversionFactor
  val symbol = "dwt"
}

object TroyOunces extends MassUnit {
  val conversionFactor: Double = 480d * TroyGrains.conversionFactor
  val symbol = "oz t"
}

object TroyPounds extends MassUnit {
  val conversionFactor: Double = 12d * TroyOunces.conversionFactor
  val symbol = "lb t"
}

object Tolas extends MassUnit {
  val conversionFactor: Double = 180d * TroyGrains.conversionFactor
  val symbol = "tola"
}

object Carats extends MassUnit {
  val conversionFactor: Double = 200d * Milligrams.conversionFactor
  val symbol = "ct"
}

object SolarMasses extends MassUnit {
  val conversionFactor = 1.98855e33
  val symbol = "M☉"
}

object EarthMasses extends MassUnit {
  val conversionFactor: Double = 5.972168e24 * Kilograms.conversionFactor
  val symbol = "M🜨"
}

object Dalton extends MassUnit {
  // Value with reference to NIST (https://physics.nist.gov/cgi-bin/cuu/Value?u)
  val conversionFactor: Double = 1.66053906660e-27 * MetricSystem.Kilo
  val symbol = "Da"
}

object ElectronVoltMass extends MassUnit {
  val conversionFactor = 1.782662e-36
  val symbol = "eV/c²"
}

object MilliElectronVoltMass extends MassUnit {
  val conversionFactor: Double = ElectronVoltMass.conversionFactor * MetricSystem.Milli
  val symbol = "meV/c²"
}

object KiloElectronVoltMass extends MassUnit {
  val conversionFactor: Double = ElectronVoltMass.conversionFactor * MetricSystem.Kilo
  val symbol = "keV/c²"
}

object MegaElectronVoltMass extends MassUnit {
  val conversionFactor: Double = ElectronVoltMass.conversionFactor * MetricSystem.Mega
  val symbol = "MeV/c²"
}

object GigaElectronVoltMass extends MassUnit {
  val conversionFactor: Double = ElectronVoltMass.conversionFactor * MetricSystem.Giga
  val symbol = "GeV/c²"
}

object TeraElectronVoltMass extends MassUnit {
  val conversionFactor: Double = ElectronVoltMass.conversionFactor * MetricSystem.Tera
  val symbol = "TeV/c²"
}

object PetaElectronVoltMass extends MassUnit {
  val conversionFactor: Double = ElectronVoltMass.conversionFactor * MetricSystem.Peta
  val symbol = "PeV/c²"
}

object ExaElectronVoltMass extends MassUnit {
  val conversionFactor: Double = ElectronVoltMass.conversionFactor * MetricSystem.Exa
  val symbol = "EeV/c²"
}
/**
 * Implicit conversions for [[squants.mass.Mass]]
 *
 * Provides support fot the DSL
 */
object MassConversions {
  lazy val nanogram: Mass = Nanograms(1)
  lazy val microgram: Mass = Micrograms(1)
  lazy val milligram: Mass = Milligrams(1)
  lazy val gram: Mass = Grams(1)
  lazy val kilogram: Mass = Kilograms(1)
  lazy val tonne: Mass = Tonnes(1)
  lazy val ounce: Mass = Ounces(1)
  lazy val pound: Mass = Pounds(1)
  lazy val kilopound: Mass = Kilopounds(1)
  lazy val megapound: Mass = Megapounds(1)
  lazy val stone: Mass = Stone(1)
  lazy val troyGrain: Mass = TroyGrains(1)
  lazy val pennyweight: Mass = Pennyweights(1)
  lazy val troyOunce: Mass = TroyOunces(1)
  lazy val troyPound: Mass = TroyPounds(1)
  lazy val tola: Mass = Tolas(1)
  lazy val carat: Mass = Carats(1)
  lazy val solarMass: Mass = SolarMasses(1)
  lazy val dalton: Mass = Dalton(1)

  lazy val eV: Mass = ElectronVoltMass(1)
  lazy val meV: Mass = MilliElectronVoltMass(1)
  lazy val keV: Mass = KiloElectronVoltMass(1)
  lazy val MeV: Mass = MegaElectronVoltMass(1)
  lazy val GeV: Mass = GigaElectronVoltMass(1)
  lazy val TeV: Mass = TeraElectronVoltMass(1)
  lazy val PeV: Mass = PetaElectronVoltMass(1)
  lazy val EeV: Mass = ExaElectronVoltMass(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def ng: Mass = Nanograms(n)
    def nanograms = ng
    def mcg: Mass = Micrograms(n)
    def micrograms = mcg
    def mg: Mass = Milligrams(n)
    def milligrams = mg
    def g: Mass = Grams(n)
    def grams = g
    def kg: Mass = Kilograms(n)
    def kilograms = kg
    def tonnes: Mass = Tonnes(n)
    def ounces: Mass = Ounces(n)
    def pounds: Mass = Pounds(n)
    def kilopounds: Mass = Kilopounds(n)
    def megapounds: Mass = Megapounds(n)
    def stone: Mass = Stone(n)
    def troyGrains: Mass = TroyGrains(n)
    def dwt: Mass = Pennyweights(n)
    def pennyweights: Mass = Pennyweights(n)
    def troyOunces: Mass = TroyOunces(n)
    def troyPounds: Mass = TroyPounds(n)
    def tolas: Mass = Tolas(n)
    def ct: Mass = Carats(n)
    def carats: Mass = Carats(n)
    def solarMasses: Mass = SolarMasses(n)
    def dalton: Mass = Dalton(n)

    def eV: Mass = ElectronVoltMass(n)
    def meV: Mass = MilliElectronVoltMass(n)
    def keV: Mass = KiloElectronVoltMass(n)
    def MeV: Mass = MegaElectronVoltMass(n)
    def GeV: Mass = GigaElectronVoltMass(n)
    def TeV: Mass = TeraElectronVoltMass(n)
    def PeV: Mass = PetaElectronVoltMass(n)
    def EeV: Mass = ExaElectronVoltMass(n)
  }

  extension (s: String) {
    def toMass: Try[Mass] = Mass(s)
  }

  given MassNumeric: AbstractQuantityNumeric[Mass](Mass.primaryUnit) {}
}

