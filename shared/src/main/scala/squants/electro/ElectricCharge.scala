/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2013-2015, Gary Keorkunian                                       **
**                                                                      **
\*                                                                      */

package squants.electro

import squants._
import squants.energy.Joules
import squants.time.{ Time, TimeIntegral }
import scala.util.Try

/**
 * @author  garyKeorkunian
 * @since   0.1
 *
 * @param value value in [[squants.electro.Coulombs]]
 */
final class ElectricCharge private (val value: Double, val unit: ElectricChargeUnit)
  extends Quantity[ElectricCharge]
  with TimeIntegral[ElectricCurrent] {

  def dimension = ElectricCharge

  protected def timeDerived: ElectricCurrent = Amperes(toCoulombs)
  protected def time: Time = Seconds(1)

  def *(that: ElectricPotential): Energy = Joules(this.toCoulombs * that.toVolts)
  def /(that: ElectricPotential): Capacitance = Farads(this.toCoulombs / that.toVolts)
  def /(that: Capacitance): ElectricPotential = Volts(this.toCoulombs / that.toFarads)
  def /(that: Length): LinearElectricChargeDensity = CoulombsPerMeter(this.toCoulombs / that.toMeters)
  def /(that: Area): AreaElectricChargeDensity = CoulombsPerSquareMeter(this.toCoulombs / that.toSquareMeters)
  def /(that: Volume): ElectricChargeDensity = CoulombsPerCubicMeter(this.toCoulombs / that.toCubicMeters)
  def /(that: Mass): ElectricChargeMassRatio = CoulombsPerKilogram(this.toCoulombs / that.toKilograms)

  def toCoulombs: Double = to(Coulombs)
  def toPicocoulombs: Double = to(Picocoulombs)
  def toNanocoulombs: Double = to(Nanocoulombs)
  def toMicrocoulombs: Double = to(Microcoulombs)
  def toMillcoulombs: Double = to(Millicoulombs)
  def toAbcoulombs: Double = to(Abcoulombs)
  def toAmpereHours: Double = to(AmpereHours)
  def toMilliampereHours: Double = to(MilliampereHours)
  def toMilliampereSeconds: Double = to(MilliampereSeconds)
}

object ElectricCharge extends Dimension[ElectricCharge] {
  private[electro] def apply[A](n: A, unit: ElectricChargeUnit)(using num: Numeric[A]) = new ElectricCharge(num.toDouble(n), unit)
  def apply(value: Any): Try[ElectricCharge] = parse(value)
  def name = "ElectricCharge"
  def primaryUnit = Coulombs
  def siUnit = Coulombs
  def units: Set[UnitOfMeasure[ElectricCharge]] = Set(Coulombs, Picocoulombs, Nanocoulombs, Microcoulombs, Millicoulombs, Abcoulombs,
    AmpereHours, MilliampereHours, MilliampereSeconds)
}

trait ElectricChargeUnit extends UnitOfMeasure[ElectricCharge] with UnitConverter {
  def apply[A](n: A)(using num: Numeric[A]): ElectricCharge = ElectricCharge(n, this)
}

object Coulombs extends ElectricChargeUnit with PrimaryUnit with SiUnit {
  val symbol = "C"
}

object Picocoulombs extends ElectricChargeUnit with SiUnit {
  val symbol = "pC"
  val conversionFactor = MetricSystem.Pico
}

object Nanocoulombs extends ElectricChargeUnit with SiUnit {
  val symbol = "nC"
  val conversionFactor = MetricSystem.Nano
}

object Microcoulombs extends ElectricChargeUnit with SiUnit {
  val symbol = "µC"
  val conversionFactor = MetricSystem.Micro
}

object Millicoulombs extends ElectricChargeUnit with SiUnit {
  val symbol = "mC"
  val conversionFactor = MetricSystem.Milli
}

object Abcoulombs extends ElectricChargeUnit {
  val symbol = "aC"
  val conversionFactor = 10d
}

object AmpereHours extends ElectricChargeUnit {
  val symbol = "Ah"
  val conversionFactor = Time.SecondsPerHour
}

object MilliampereHours extends ElectricChargeUnit {
  val symbol = "mAh"
  val conversionFactor: Double = AmpereHours.conversionFactor * MetricSystem.Milli
}

object MilliampereSeconds extends ElectricChargeUnit {
  val symbol = "mAs"
  val conversionFactor: Double = Coulombs.conversionFactor * MetricSystem.Milli
}

object ElectricChargeConversions {
  lazy val coulomb: ElectricCharge = Coulombs(1)
  lazy val picocoulomb: ElectricCharge = Picocoulombs(1)
  lazy val nanocoulomb: ElectricCharge = Nanocoulombs(1)
  lazy val microcoulomb: ElectricCharge = Microcoulombs(1)
  lazy val millicoulomb: ElectricCharge = Millicoulombs(1)
  lazy val abcoulomb: ElectricCharge = Abcoulombs(1)
  lazy val ampereHour: ElectricCharge = AmpereHours(1)
  lazy val milliampereHour: ElectricCharge = MilliampereHours(1)
  lazy val milliampereSecond: ElectricCharge = MilliampereSeconds(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def coulombs: ElectricCharge = Coulombs(n)
    def picocoulombs: ElectricCharge = Picocoulombs(n)
    def nanocoulombs: ElectricCharge = Nanocoulombs(n)
    def microcoulombs: ElectricCharge = Microcoulombs(n)
    def millicoulombs: ElectricCharge = Millicoulombs(n)
    def abcoulombs: ElectricCharge = Abcoulombs(n)
    def ampereHours: ElectricCharge = AmpereHours(n)
    def milliampereHours: ElectricCharge = MilliampereHours(n)
    def milliampereSeconds: ElectricCharge = MilliampereSeconds(n)
  }

  given ElectricalChargeNumeric: AbstractQuantityNumeric[ElectricCharge](ElectricCharge.primaryUnit) {}
}
