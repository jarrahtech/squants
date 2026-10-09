/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2013-2015, Gary Keorkunian                                       **
**                                                                      **
\*                                                                      */

package squants.electro

import squants._
import scala.util.Try

/**
 * @author  garyKeorkunian
 * @since   0.1
 *
 * @param value value in [[squants.electro.Henry]]
 */
final class Inductance private (val value: Double, val unit: InductanceUnit)
  extends Quantity[Inductance] {

  def dimension = Inductance

  def *(that: ElectricCurrent): MagneticFlux = Webers(this.toHenry * that.toAmperes)
  def /(that: Length): Permeability = HenriesPerMeter(this.toHenry / that.toMeters)

  def toHenry: Double = to(Henry)
  def toMillihenry: Double = to(Millihenry)
  def toMicrohenry: Double = to(Microhenry)
  def toNanohenry: Double = to(Nanohenry)
  def toPicohenry: Double = to(Picohenry)
}

object Inductance extends Dimension[Inductance] {
  private[electro] def apply[A](n: A, unit: InductanceUnit)(using num: Numeric[A]) = new Inductance(num.toDouble(n), unit)
  def apply(value: Any): Try[Inductance] = parse(value)
  def name = "Inductance"
  def primaryUnit = Henry
  def siUnit = Henry
  def units: Set[UnitOfMeasure[Inductance]] = Set(Henry, Millihenry, Microhenry, Nanohenry, Picohenry)
}

trait InductanceUnit extends UnitOfMeasure[Inductance] with UnitConverter {
  def apply[A](n: A)(using num: Numeric[A]): Inductance = Inductance(n, this)
}

object Henry extends InductanceUnit with PrimaryUnit with SiUnit {
  val symbol = "H"
}

object Millihenry extends InductanceUnit with SiUnit {
  val symbol = "mH"
  val conversionFactor = MetricSystem.Milli
}

object Microhenry extends InductanceUnit with SiUnit {
  val symbol = "μH"
  val conversionFactor = MetricSystem.Micro
}

object Nanohenry extends InductanceUnit with SiUnit {
  val symbol = "nH"
  val conversionFactor = MetricSystem.Nano
}

object Picohenry extends InductanceUnit with SiUnit {
  val symbol = "pH"
  val conversionFactor = MetricSystem.Pico
}

object InductanceConversions {
  lazy val henry: Inductance = Henry(1)
  lazy val millihenry: Inductance = Millihenry(1)
  lazy val microhenry: Inductance = Microhenry(1)
  lazy val nanohenry: Inductance = Nanohenry(1)
  lazy val picohenry: Inductance = Picohenry(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def henry: Inductance = Henry(n)
    def millihenry: Inductance = Millihenry(n)
    def microhenry: Inductance = Microhenry(n)
    def nanohenry: Inductance = Nanohenry(n)
    def picohenry: Inductance = Picohenry(n)
  }

  given InductanceNumeric: AbstractQuantityNumeric[Inductance](Inductance.primaryUnit) {}
}