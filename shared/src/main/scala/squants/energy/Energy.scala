/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2013-2015, Gary Keorkunian                                       **
**                                                                      **
\*                                                                      */

package squants.energy

import squants._
import squants.electro.{ Coulombs, ElectricCharge, ElectricPotential, Volts }
import squants.mass.{ ChemicalAmount, Kilograms }
import squants.motion.{ NewtonMeters, Newtons, Torque }
import squants.space.CubicMeters
import squants.thermal.{ JoulesPerKelvin, Kelvin, ThermalCapacity }
import squants.time.{ Time, _ }
import squants.radio.{ Irradiance, ParticleFlux, WattsPerSquareMeter }
import scala.util.Try

/**
 * Represents a quantity of energy
 *
 * @author  garyKeorkunian
 * @since   0.1
 *
 * @param value value in [[squants.energy.WattHours]]
 */
final class Energy private (val value: Double, val unit: EnergyUnit)
  extends Quantity[Energy]
  with TimeIntegral[Power]
  with SecondTimeIntegral[PowerRamp] {

  def dimension = Energy

  protected def timeDerived: Power = Watts(toWattHours)
  protected def time: Time = Hours(1)

  def *(that: ParticleFlux): Irradiance = WattsPerSquareMeter(
    Hours(1).toSeconds * this.toWattHours *
      that.toBecquerelsPerSquareMeterSecond)
  def /(that: Length): Force = Newtons(this.toJoules / that.toMeters)
  def /(that: Force): Length = Meters(this.toJoules / that.toNewtons)
  def /(that: Mass): SpecificEnergy = Grays(this.toJoules / that.toKilograms)
  def /(that: SpecificEnergy): Mass = Kilograms(this.toJoules / that.toGrays)
  def /(that: Volume): EnergyDensity = JoulesPerCubicMeter(this.toJoules / that.toCubicMeters)
  def /(that: EnergyDensity): Volume = CubicMeters(this.toJoules / that.toJoulesPerCubicMeter)
  def /(that: ElectricCharge): ElectricPotential = Volts(this.toJoules / that.toCoulombs)
  def /(that: ElectricPotential): ElectricCharge = Coulombs(this.toJoules / that.toVolts)
  def /(that: Temperature): ThermalCapacity = JoulesPerKelvin(this.toJoules / that.toKelvinDegrees)
  def /(that: ThermalCapacity): squants.thermal.Temperature = Kelvin(this.toJoules / that.toJoulesPerKelvin)

  def /(that: ChemicalAmount): MolarEnergy = JoulesPerMole(this.toJoules / that.toMoles)
  def /(that: Angle): Torque = NewtonMeters(toJoules / that.toRadians)
  // def /(that: Area) = ??? // Insolation, Energy Area Density

  def /(that: TimeSquared): PowerRamp = this / that.time1 / that.time2
  def /(that: PowerRamp): TimeSquared = (this / that.timeIntegrated) * time

  def toWattHours: Double = to(WattHours)
  def toMilliwattHours: Double = to(MilliwattHours)
  def toKilowattHours: Double = to(KilowattHours)
  def toMegawattHours: Double = to(MegawattHours)
  def toGigawattHours: Double = to(GigawattHours)

  def toJoules: Double = to(Joules)
  def toPicojoules: Double = to(Picojoules)
  def toNanojoules: Double = to(Nanojoules)
  def toMicrojoules: Double = to(Microjoules)
  def toMillijoules: Double = to(Millijoules)
  def toKilojoules: Double = to(Kilojoules)
  def toMegajoules: Double = to(Megajoules)
  def toGigajoules: Double = to(Gigajoules)
  def toTerajoules: Double = to(Terajoules)

  def toeV: Double = to(ElectronVolt)
  def tomeV: Double = to(MilliElectronVolt)
  def tokeV: Double = to(KiloElectronVolt)
  def toMeV: Double = to(MegaElectronVolt)
  def toGeV: Double = to(GigaElectronVolt)
  def toTeV: Double = to(TeraElectronVolt)
  def toPeV: Double = to(PetaElectronVolt)
  def toEeV: Double = to(ExaElectronVolt)

  def toBtus: Double = to(BritishThermalUnits)
  def toMBtus: Double = to(MBtus)
  def toMMBtus: Double = to(MMBtus)
  def toErgs: Double = to(Ergs)

  /**
   * Energy and torque have the same unit, so convert appropriately
   * @return numerically equivalent value in newton-meters
   */
  def asTorque: Torque = NewtonMeters(toJoules)
}

/**
 * Companion object for [[squants.energy.Energy]]
 */
object Energy extends Dimension[Energy] {
  private[energy] def apply[A](n: A, unit: EnergyUnit)(using num: Numeric[A]) = new Energy(num.toDouble(n), unit)
  def apply(load: Power, time: Time): Energy = load * time
  def apply(value: Any): Try[Energy] = parse(value)

  def name = "Energy"
  def primaryUnit = WattHours
  def siUnit = Joules
  def units: Set[UnitOfMeasure[Energy]] = Set(WattHours, MilliwattHours, KilowattHours, MegawattHours, GigawattHours,
    Joules, Picojoules, Nanojoules, Microjoules, Millijoules,
    Kilojoules, Megajoules, Gigajoules, Terajoules,
    BritishThermalUnits, MBtus, MMBtus, Ergs,
    ElectronVolt, MilliElectronVolt, KiloElectronVolt, MegaElectronVolt,
    GigaElectronVolt, TeraElectronVolt, PetaElectronVolt, ExaElectronVolt)
}

/**
 * Base trait for units of [[squants.energy.Energy]]
 */
trait EnergyUnit extends UnitOfMeasure[Energy] with UnitConverter {
  def apply[A](n: A)(using num: Numeric[A]): Energy = Energy(n, this)
}

object WattHours extends EnergyUnit with PrimaryUnit {
  val symbol = "Wh"
}

object MilliwattHours extends EnergyUnit {
  val conversionFactor: Double = Watts.conversionFactor * MetricSystem.Milli
  val symbol = "mWh"
}

object KilowattHours extends EnergyUnit {
  val conversionFactor: Double = Watts.conversionFactor * MetricSystem.Kilo
  val symbol = "kWh"
}

object MegawattHours extends EnergyUnit {
  val conversionFactor: Double = Watts.conversionFactor * MetricSystem.Mega
  val symbol = "MWh"
}

object GigawattHours extends EnergyUnit {
  val conversionFactor: Double = Watts.conversionFactor * MetricSystem.Giga
  val symbol = "GWh"
}

object Joules extends EnergyUnit with SiUnit {
  val conversionFactor: Double = 1.0 / Time.SecondsPerHour
  val symbol = "J"
}

object Picojoules extends EnergyUnit with SiUnit {
  val conversionFactor: Double = Joules.conversionFactor * MetricSystem.Pico
  val symbol = "pJ"
}

object Nanojoules extends EnergyUnit with SiUnit {
  val conversionFactor: Double = Joules.conversionFactor * MetricSystem.Nano
  val symbol = "nJ"
}

object Microjoules extends EnergyUnit with SiUnit {
  val conversionFactor: Double = Joules.conversionFactor * MetricSystem.Micro
  val symbol = "µJ"
}

object Millijoules extends EnergyUnit with SiUnit {
  val conversionFactor: Double = Joules.conversionFactor * MetricSystem.Milli
  val symbol = "mJ"
}

object Kilojoules extends EnergyUnit with SiUnit {
  val conversionFactor: Double = Joules.conversionFactor * MetricSystem.Kilo
  val symbol = "kJ"
}

object Megajoules extends EnergyUnit with SiUnit {
  val conversionFactor: Double = Joules.conversionFactor * MetricSystem.Mega
  val symbol = "MJ"
}

object Gigajoules extends EnergyUnit with SiUnit {
  val conversionFactor: Double = Joules.conversionFactor * MetricSystem.Giga
  val symbol = "GJ"
}

object Terajoules extends EnergyUnit with SiUnit {
  val conversionFactor: Double = Joules.conversionFactor * MetricSystem.Tera
  val symbol = "TJ"
}

object BritishThermalUnits extends EnergyUnit {
  val conversionFactor = EnergyConversions.btuMultiplier
  val symbol = "Btu"
}

object MBtus extends EnergyUnit {
  val conversionFactor: Double = EnergyConversions.btuMultiplier * MetricSystem.Kilo
  val symbol = "MBtu"
}

object MMBtus extends EnergyUnit {
  val conversionFactor: Double = EnergyConversions.btuMultiplier * MetricSystem.Mega
  val symbol = "MMBtu"
}

object Ergs extends EnergyUnit {
  val conversionFactor: Double = 100.0 * Nanojoules.conversionFactor
  val symbol = "erg"
}

object ElectronVolt extends EnergyUnit {
  val conversionFactor: Double = Joules.conversionFactor * 1.602176565e-19
  val symbol = "eV"
}

object MilliElectronVolt extends EnergyUnit {
  val conversionFactor: Double = ElectronVolt.conversionFactor * MetricSystem.Milli
  val symbol = "meV"
}

object KiloElectronVolt extends EnergyUnit {
  val conversionFactor: Double = ElectronVolt.conversionFactor * MetricSystem.Kilo
  val symbol = "keV"
}

object MegaElectronVolt extends EnergyUnit {
  val conversionFactor: Double = ElectronVolt.conversionFactor * MetricSystem.Mega
  val symbol = "MeV"
}

object GigaElectronVolt extends EnergyUnit {
  val conversionFactor: Double = ElectronVolt.conversionFactor * MetricSystem.Giga
  val symbol = "GeV"
}

object TeraElectronVolt extends EnergyUnit {
  val conversionFactor: Double = ElectronVolt.conversionFactor * MetricSystem.Tera
  val symbol = "TeV"
}

object PetaElectronVolt extends EnergyUnit {
  val conversionFactor: Double = ElectronVolt.conversionFactor * MetricSystem.Peta
  val symbol = "PeV"
}

object ExaElectronVolt extends EnergyUnit {
  val conversionFactor: Double = ElectronVolt.conversionFactor * MetricSystem.Exa
  val symbol = "EeV"
}

object EnergyConversions {
  lazy val wattHour: Energy = WattHours(1)
  lazy val Wh = wattHour
  lazy val milliwattHour: Energy = MilliwattHours(1)
  lazy val mWh = milliwattHour
  lazy val kilowattHour: Energy = KilowattHours(1)
  lazy val kWh = kilowattHour
  lazy val megawattHour: Energy = MegawattHours(1)
  lazy val MWh = megawattHour
  lazy val gigawattHour: Energy = GigawattHours(1)
  lazy val GWh = gigawattHour

  lazy val joule: Energy = Joules(1)
  lazy val picojoule: Energy = Picojoules(1)
  lazy val nanojoule: Energy = Nanojoules(1)
  lazy val microjoule: Energy = Microjoules(1)
  lazy val millijoule: Energy = Millijoules(1)
  lazy val kilojoule: Energy = Kilojoules(1)
  lazy val megajoule: Energy = Megajoules(1)
  lazy val gigajoule: Energy = Gigajoules(1)
  lazy val terajoule: Energy = Terajoules(1)

  lazy val btu: Energy = BritishThermalUnits(1)
  lazy val btuMultiplier = 2.930710701722222e-1

  lazy val eV: Energy = ElectronVolt(1)
  lazy val meV: Energy = MilliElectronVolt(1)
  lazy val keV: Energy = KiloElectronVolt(1)
  lazy val MeV: Energy = MegaElectronVolt(1)
  lazy val GeV: Energy = GigaElectronVolt(1)
  lazy val TeV: Energy = TeraElectronVolt(1)
  lazy val PeV: Energy = PetaElectronVolt(1)
  lazy val EeV: Energy = ExaElectronVolt(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def J: Energy = Joules(n)
    def joules: Energy = Joules(n)
    def pJ: Energy = Picojoules(n)
    def picojoules: Energy = Picojoules(n)
    def nJ: Energy = Nanojoules(n)
    def nanojoules: Energy = Nanojoules(n)
    def µJ: Energy = Microjoules(n)
    def microjoules: Energy = Microjoules(n)
    def mJ: Energy = Millijoules(n)
    def milljoules: Energy = Millijoules(n)
    def kJ: Energy = Kilojoules(n)
    def kilojoules: Energy = Kilojoules(n)
    def MJ: Energy = Megajoules(n)
    def megajoules: Energy = Megajoules(n)
    def GJ: Energy = Gigajoules(n)
    def gigajoules: Energy = Gigajoules(n)
    def TJ: Energy = Terajoules(n)
    def terajoules: Energy = Terajoules(n)

    def Wh: Energy = WattHours(n)
    def mWh: Energy = MilliwattHours(n)
    def kWh: Energy = KilowattHours(n)
    def MWh: Energy = MegawattHours(n)
    def GWh: Energy = GigawattHours(n)
    def Btu: Energy = BritishThermalUnits(n)
    def MBtu: Energy = MBtus(n)
    def MMBtu: Energy = MMBtus(n)
    def ergs: Energy = Ergs(n)
    def wattHours: Energy = WattHours(n)
    def kilowattHours: Energy = KilowattHours(n)
    def megawattHours: Energy = MegawattHours(n)
    def gigawattHours: Energy = GigawattHours(n)

    def eV: Energy = ElectronVolt(n)
    def meV: Energy = MilliElectronVolt(n)
    def keV: Energy = KiloElectronVolt(n)
    def MeV: Energy = MegaElectronVolt(n)
    def GeV: Energy = GigaElectronVolt(n)
    def TeV: Energy = TeraElectronVolt(n)
    def PeV: Energy = PetaElectronVolt(n)
    def EeV: Energy = ExaElectronVolt(n)
  }

  extension (s: String) {
    def toEnergy: Try[Energy] = Energy(s)
  }

  given EnergyNumeric: AbstractQuantityNumeric[Energy](Energy.primaryUnit) {}
}
