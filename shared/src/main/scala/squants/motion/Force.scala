/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2013-2015, Gary Keorkunian                                       **
**                                                                      **
\*                                                                      */

package squants.motion

import squants._
import squants.energy.Joules
import squants.mass.{ Kilograms, Pounds }
import squants.space.SquareMeters
import squants.time.{ Seconds, TimeDerivative, TimeIntegral }
import scala.util.Try

/**
 * @author  garyKeorkunian
 * @since   0.1
 *
 * @param value Double
 */
final class Force private (val value: Double, val unit: ForceUnit)
  extends Quantity[Force]
  with TimeDerivative[Momentum] with TimeIntegral[Yank] {

  def dimension = Force

  protected[squants] def timeIntegrated: Momentum = NewtonSeconds(toNewtons)
  protected def timeDerived: Yank = NewtonsPerSecond(toNewtons)
  override def time: Time = Seconds(1)

  /* This could also be Torque, as Energy(Work) and Torque are dimensionally equivalent */
  def *(that: Length): Energy = Joules(this.toNewtons * that.toMeters)
  def /(that: Length) = ??? // return SurfaceTension
  def /(that: Mass): Acceleration = MetersPerSecondSquared(this.toNewtons / that.toKilograms)
  def /(that: Acceleration): Mass = Kilograms(this.toNewtons / that.toMetersPerSecondSquared)
  def /(that: Area): Pressure = Pascals(this.toNewtons / that.toSquareMeters)
  def /(that: Pressure): Area = SquareMeters(this.toNewtons / that.toPascals)

  def toNewtons: Double = to(Newtons)
  def toKilogramForce: Double = to(KilogramForce)
  def toPoundForce: Double = to(PoundForce)
  def toKiloElectronVoltsPerMicrometer: Double = to(KiloElectronVoltsPerMicrometer)
  def toMegaElectronVoltsPerCentimeter: Double = to(MegaElectronVoltsPerCentimeter)
}

object Force extends Dimension[Force] {
  private[motion] def apply[A](n: A, unit: ForceUnit)(using num: Numeric[A]) = new Force(num.toDouble(n), unit)
  def apply(value: Any): Try[Force] = parse(value)
  def name = "Force"
  def primaryUnit = Newtons
  def siUnit = Newtons
  def units: Set[UnitOfMeasure[Force]] = Set(
    Newtons, KilogramForce, PoundForce,
    KiloElectronVoltsPerMicrometer, MegaElectronVoltsPerCentimeter)
}

trait ForceUnit extends UnitOfMeasure[Force] with UnitConverter {
  def apply[A](n: A)(using num: Numeric[A]): Force = Force(n, this)
}

object Newtons extends ForceUnit with PrimaryUnit with SiUnit {
  val symbol = "N"
}

object KilogramForce extends ForceUnit {
  val symbol = "kgf"
  val conversionFactor: Double = MetersPerSecondSquared.conversionFactor * EarthGravities.conversionFactor
}

object PoundForce extends ForceUnit {
  val symbol = "lbf"
  val conversionFactor: Double = Pounds.conversionFactor * KilogramForce.conversionFactor / Kilograms.conversionFactor
}

object KiloElectronVoltsPerMicrometer extends ForceUnit {
  val symbol = "keV/μm"
  val conversionFactor: Double = 1.602176565e-16 / MetricSystem.Micro
}

object MegaElectronVoltsPerCentimeter extends ForceUnit {
  val symbol = "MeV/cm"
  val conversionFactor: Double = 1.602176565e-13 / MetricSystem.Centi
}

object ForceConversions {
  lazy val newton: Force = Newtons(1)
  lazy val kilogramForce: Force = KilogramForce(1)
  lazy val poundForce: Force = PoundForce(1)
  lazy val kiloElectronVoltsPerMicrometer: Force = KiloElectronVoltsPerMicrometer(1)
  lazy val megaElectronVoltsPerCentimeter: Force = MegaElectronVoltsPerCentimeter(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def newtons: Force = Newtons(n)
    def kilogramForce: Force = KilogramForce(n)
    def poundForce: Force = PoundForce(n)
    def lbf: Force = PoundForce(n)
    def kiloElectronVoltsPerMicrometer: Force = KiloElectronVoltsPerMicrometer(n)
    def megaElectronVoltsPerCentimeter: Force = MegaElectronVoltsPerCentimeter(n)
  }

  given ForceNumeric: AbstractQuantityNumeric[Force](Force.primaryUnit) {}
}

