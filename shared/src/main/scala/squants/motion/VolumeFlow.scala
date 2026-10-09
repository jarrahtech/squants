/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2013-2015, Gary Keorkunian                                       **
**                                                                      **
\*                                                                      */

package squants.motion

import squants.{ Seconds, _ }
import squants.space.{ CubicFeet, CubicMeters, Litres, Microlitres, Millilitres, Nanolitres, UsGallons }
import squants.time._
import scala.util.Try

/**
 * @author  garyKeorkunian
 * @since   0.1
 *
 * @param value Double
 */
final class VolumeFlow private (val value: Double, val unit: VolumeFlowRateUnit)
  extends Quantity[VolumeFlow]
  with TimeDerivative[Volume] {

  def dimension = VolumeFlow

  protected[squants] def timeIntegrated: Volume = CubicMeters(toCubicMetersPerSecond)
  protected[squants] def time: Time = Seconds(1)

  def toCubicMetersPerSecond: Double = to(CubicMetersPerSecond)
  def toLitresPerDay: Double = to(LitresPerDay)
  def toLitresPerHour: Double = to(LitresPerHour)
  def toLitresPerMinute: Double = to(LitresPerMinute)
  def toLitresPerSecond: Double = to(LitresPerSecond)
  def toNanolitresPerDay: Double = to(NanolitresPerDay)
  def toNanolitresPerHour: Double = to(NanolitresPerHour)
  def toNanolitresPerMinute: Double = to(NanolitresPerMinute)
  def toNanolitresPerSecond: Double = to(NanolitresPerSecond)
  def toMicrolitresPerDay: Double = to(MicrolitresPerDay)
  def toMicrolitresPerHour: Double = to(MicrolitresPerHour)
  def toMicrolitresPerMinute: Double = to(MicrolitresPerMinute)
  def toMicrolitresPerSecond: Double = to(MicrolitresPerSecond)
  def toMillilitresPerDay: Double = to(MillilitresPerDay)
  def toMillilitresPerHour: Double = to(MillilitresPerHour)
  def toMillilitresPerMinute: Double = to(MillilitresPerMinute)
  def toMillilitresPerSecond: Double = to(MillilitresPerSecond)

  def toCubicFeetPerDay: Double = to(CubicFeetPerDay)
  def toCubicFeetPerHour: Double = to(CubicFeetPerHour)
  def toCubicFeetPerMinute: Double = to(CubicFeetPerMinute)
  def toCubicFeetPerSecond: Double = to(CubicFeetPerSecond)
  def toGallonsPerDay: Double = to(GallonsPerDay)
  def toGallonsPerHour: Double = to(GallonsPerHour)
  def toGallonsPerMinute: Double = to(GallonsPerMinute)
  def toGallonsPerSecond: Double = to(GallonsPerSecond)
}

object VolumeFlow extends Dimension[VolumeFlow] {
  private[motion] def apply[A](n: A, unit: VolumeFlowRateUnit)(using num: Numeric[A]) = new VolumeFlow(num.toDouble(n), unit)
  def apply(value: Any): Try[VolumeFlow] = parse(value)
  def name = "VolumeFlow"
  def primaryUnit = CubicMetersPerSecond
  def siUnit = CubicMetersPerSecond
  def units: Set[UnitOfMeasure[VolumeFlow]] = Set(CubicMetersPerSecond, LitresPerSecond, LitresPerMinute, LitresPerHour, LitresPerDay,
    NanolitresPerSecond, NanolitresPerMinute, NanolitresPerHour, NanolitresPerDay,
    MicrolitresPerSecond, MicrolitresPerMinute, MicrolitresPerHour, MicrolitresPerDay,
    MillilitresPerSecond, MillilitresPerMinute, MillilitresPerHour, MillilitresPerDay,
    CubicFeetPerDay, CubicFeetPerHour, CubicFeetPerMinute, CubicFeetPerSecond,
    GallonsPerDay, GallonsPerHour, GallonsPerMinute, GallonsPerSecond)
}

trait VolumeFlowRateUnit extends UnitOfMeasure[VolumeFlow] with UnitConverter {
  def apply[A](n: A)(using num: Numeric[A]): VolumeFlow = VolumeFlow(n, this)
}

object CubicMetersPerSecond extends VolumeFlowRateUnit with PrimaryUnit with SiUnit {
  val symbol = "m³/s"
}

object LitresPerSecond extends VolumeFlowRateUnit with SiUnit {
  val symbol = "L/s"
  val conversionFactor: Double = Litres.conversionFactor / CubicMeters.conversionFactor
}

object LitresPerMinute extends VolumeFlowRateUnit with SiUnit {
  val symbol = "L/min"
  val conversionFactor: Double = Litres.conversionFactor / CubicMeters.conversionFactor / Time.SecondsPerMinute
}

object LitresPerHour extends VolumeFlowRateUnit with SiUnit {
  val symbol = "L/h"
  val conversionFactor: Double = Litres.conversionFactor / CubicMeters.conversionFactor / Time.SecondsPerHour
}

object LitresPerDay extends VolumeFlowRateUnit with SiUnit {
  val symbol = "L/d"
  val conversionFactor: Double = Litres.conversionFactor / CubicMeters.conversionFactor / Time.SecondsPerDay
}

object NanolitresPerSecond extends VolumeFlowRateUnit with SiUnit {
  val symbol = "nl/s"
  val conversionFactor: Double = Nanolitres.conversionFactor / CubicMeters.conversionFactor
}

object NanolitresPerMinute extends VolumeFlowRateUnit with SiUnit {
  val symbol = "nl/min"
  val conversionFactor: Double = (Nanolitres.conversionFactor / CubicMeters.conversionFactor) / Time.SecondsPerMinute
}

object NanolitresPerHour extends VolumeFlowRateUnit with SiUnit {
  val symbol = "nl/h"
  val conversionFactor: Double = Nanolitres.conversionFactor / CubicMeters.conversionFactor / Time.SecondsPerHour
}

object NanolitresPerDay extends VolumeFlowRateUnit with SiUnit {
  val symbol = "nl/d"
  val conversionFactor: Double = Nanolitres.conversionFactor / CubicMeters.conversionFactor / Time.SecondsPerDay
}

object MicrolitresPerSecond extends VolumeFlowRateUnit with SiUnit {
  val symbol = "µl/s"
  val conversionFactor: Double = Microlitres.conversionFactor / CubicMeters.conversionFactor
}

object MicrolitresPerMinute extends VolumeFlowRateUnit with SiUnit {
  val symbol = "µl/min"
  val conversionFactor: Double = (Microlitres.conversionFactor / CubicMeters.conversionFactor) / Time.SecondsPerMinute
}

object MicrolitresPerHour extends VolumeFlowRateUnit with SiUnit {
  val symbol = "µl/h"
  val conversionFactor: Double = Microlitres.conversionFactor / CubicMeters.conversionFactor / Time.SecondsPerHour
}

object MicrolitresPerDay extends VolumeFlowRateUnit with SiUnit {
  val symbol = "µl/d"
  val conversionFactor: Double = Microlitres.conversionFactor / CubicMeters.conversionFactor / Time.SecondsPerDay
}

object MillilitresPerSecond extends VolumeFlowRateUnit with SiUnit {
  val symbol = "ml/s"
  val conversionFactor: Double = Millilitres.conversionFactor / CubicMeters.conversionFactor
}

object MillilitresPerMinute extends VolumeFlowRateUnit with SiUnit {
  val symbol = "ml/min"
  val conversionFactor: Double = (Millilitres.conversionFactor / CubicMeters.conversionFactor) / Time.SecondsPerMinute
}

object MillilitresPerHour extends VolumeFlowRateUnit with SiUnit {
  val symbol = "ml/h"
  val conversionFactor: Double = Millilitres.conversionFactor / CubicMeters.conversionFactor / Time.SecondsPerHour
}

object MillilitresPerDay extends VolumeFlowRateUnit with SiUnit {
  val symbol = "ml/d"
  val conversionFactor: Double = Millilitres.conversionFactor / CubicMeters.conversionFactor / Time.SecondsPerDay
}

object CubicFeetPerDay extends VolumeFlowRateUnit {
  val symbol = "ft³/d"
  val conversionFactor: Double = (CubicFeet.conversionFactor / CubicMeters.conversionFactor) / Time.SecondsPerDay
}

object CubicFeetPerHour extends VolumeFlowRateUnit {
  val symbol = "ft³/hr"
  val conversionFactor: Double = (CubicFeet.conversionFactor / CubicMeters.conversionFactor) / Time.SecondsPerHour
}

object CubicFeetPerMinute extends VolumeFlowRateUnit {
  val symbol = "ft³/m"
  val conversionFactor: Double = (CubicFeet.conversionFactor / CubicMeters.conversionFactor) / Time.SecondsPerMinute
}

object CubicFeetPerSecond extends VolumeFlowRateUnit {
  val symbol = "cfs"
  val conversionFactor: Double = CubicFeet.conversionFactor / CubicMeters.conversionFactor
}

object GallonsPerDay extends VolumeFlowRateUnit {
  val symbol = "GPD"
  val conversionFactor: Double = (UsGallons.conversionFactor / CubicMeters.conversionFactor) / Time.SecondsPerDay
}

object GallonsPerHour extends VolumeFlowRateUnit {
  val symbol = "GPH"
  val conversionFactor: Double = (UsGallons.conversionFactor / CubicMeters.conversionFactor) / Time.SecondsPerHour
}

object GallonsPerMinute extends VolumeFlowRateUnit {
  val symbol = "GPM"
  val conversionFactor: Double = (UsGallons.conversionFactor / CubicMeters.conversionFactor) / Time.SecondsPerMinute
}

object GallonsPerSecond extends VolumeFlowRateUnit {
  val symbol = "GPS"
  val conversionFactor: Double = UsGallons.conversionFactor / CubicMeters.conversionFactor
}

object VolumeFlowConversions {
  lazy val cubicMeterPerSecond: VolumeFlow = CubicMetersPerSecond(1)
  lazy val litresPerSecond: VolumeFlow = LitresPerSecond(1)
  lazy val litersPerSecond: VolumeFlow = LitresPerSecond(1)
  lazy val litersPerMinute: VolumeFlow = LitresPerMinute(1)
  lazy val litresPerMinute: VolumeFlow = LitresPerMinute(1)
  lazy val litersPerHour: VolumeFlow = LitresPerHour(1)
  lazy val litresPerHour: VolumeFlow = LitresPerHour(1)
  lazy val litersPerDay: VolumeFlow = LitresPerDay(1)
  lazy val litresPerDay: VolumeFlow = LitresPerDay(1)
  lazy val nanolitresPerSecond: VolumeFlow = NanolitresPerSecond(1)
  lazy val nanolitersPerSecond: VolumeFlow = NanolitresPerSecond(1)
  lazy val nanolitersPerMinute: VolumeFlow = NanolitresPerMinute(1)
  lazy val nanolitresPerMinute: VolumeFlow = NanolitresPerMinute(1)
  lazy val nanolitersPerHour: VolumeFlow = NanolitresPerHour(1)
  lazy val nanolitresPerHour: VolumeFlow = NanolitresPerHour(1)
  lazy val nanolitersPerDay: VolumeFlow = NanolitresPerDay(1)
  lazy val nanolitresPerDay: VolumeFlow = NanolitresPerDay(1)
  lazy val microlitresPerSecond: VolumeFlow = MicrolitresPerSecond(1)
  lazy val microlitersPerSecond: VolumeFlow = MicrolitresPerSecond(1)
  lazy val microlitersPerMinute: VolumeFlow = MicrolitresPerMinute(1)
  lazy val microlitresPerMinute: VolumeFlow = MicrolitresPerMinute(1)
  lazy val microlitersPerHour: VolumeFlow = MicrolitresPerHour(1)
  lazy val microlitresPerHour: VolumeFlow = MicrolitresPerHour(1)
  lazy val microlitersPerDay: VolumeFlow = MicrolitresPerDay(1)
  lazy val microlitresPerDay: VolumeFlow = MicrolitresPerDay(1)
  lazy val millilitresPerSecond: VolumeFlow = MillilitresPerSecond(1)
  lazy val millilitersPerSecond: VolumeFlow = MillilitresPerSecond(1)
  lazy val millilitersPerMinute: VolumeFlow = MillilitresPerMinute(1)
  lazy val millilitresPerMinute: VolumeFlow = MillilitresPerMinute(1)
  lazy val millilitersPerHour: VolumeFlow = MillilitresPerHour(1)
  lazy val millilitresPerHour: VolumeFlow = MillilitresPerHour(1)
  lazy val millilitersPerDay: VolumeFlow = MillilitresPerDay(1)
  lazy val millilitresPerDay: VolumeFlow = MillilitresPerDay(1)
  lazy val cubicFeetPerDay: VolumeFlow = CubicFeetPerDay(1)
  lazy val cubicFeetPerHour: VolumeFlow = CubicFeetPerHour(1)
  lazy val cubicFeetPerMinute: VolumeFlow = CubicFeetPerMinute(1)
  lazy val cubicFeetPerSecond: VolumeFlow = CubicFeetPerSecond(1)
  lazy val gallonPerDay: VolumeFlow = GallonsPerDay(1)
  lazy val gallonPerHour: VolumeFlow = GallonsPerHour(1)
  lazy val gallonPerMinute: VolumeFlow = GallonsPerMinute(1)
  lazy val gallonPerSecond: VolumeFlow = GallonsPerSecond(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def cubicMetersPerSecond: VolumeFlow = CubicMetersPerSecond(n)
    def litresPerSecond: VolumeFlow = LitresPerSecond(n)
    def litersPerSecond: VolumeFlow = LitresPerSecond(n)
    def litersPerMinute: VolumeFlow = LitresPerMinute(n)
    def litresPerMinute: VolumeFlow = LitresPerMinute(n)
    def litersPerHour: VolumeFlow = LitresPerHour(n)
    def litresPerHour: VolumeFlow = LitresPerHour(n)
    def litersPerDay: VolumeFlow = LitresPerDay(n)
    def litresPerDay: VolumeFlow = LitresPerDay(n)
    def nanolitresPerSecond: VolumeFlow = NanolitresPerSecond(n)
    def nanolitersPerSecond: VolumeFlow = NanolitresPerSecond(n)
    def nanolitersPerMinute: VolumeFlow = NanolitresPerMinute(n)
    def nanolitresPerMinute: VolumeFlow = NanolitresPerMinute(n)
    def nanolitersPerHour: VolumeFlow = NanolitresPerHour(n)
    def nanolitresPerHour: VolumeFlow = NanolitresPerHour(n)
    def nanolitersPerDay: VolumeFlow = NanolitresPerDay(n)
    def nanolitresPerDay: VolumeFlow = NanolitresPerDay(n)
    def microlitresPerSecond: VolumeFlow = MicrolitresPerSecond(n)
    def microlitersPerSecond: VolumeFlow = MicrolitresPerSecond(n)
    def microlitersPerMinute: VolumeFlow = MicrolitresPerMinute(n)
    def microlitresPerMinute: VolumeFlow = MicrolitresPerMinute(n)
    def microlitersPerHour: VolumeFlow = MicrolitresPerHour(n)
    def microlitresPerHour: VolumeFlow = MicrolitresPerHour(n)
    def microlitersPerDay: VolumeFlow = MicrolitresPerDay(n)
    def microlitresPerDay: VolumeFlow = MicrolitresPerDay(n)
    def millilitresPerSecond: VolumeFlow = MillilitresPerSecond(n)
    def millilitersPerSecond: VolumeFlow = MillilitresPerSecond(n)
    def millilitersPerMinute: VolumeFlow = MillilitresPerMinute(n)
    def millilitresPerMinute: VolumeFlow = MillilitresPerMinute(n)
    def millilitersPerHour: VolumeFlow = MillilitresPerHour(n)
    def millilitresPerHour: VolumeFlow = MillilitresPerHour(n)
    def millilitersPerDay: VolumeFlow = MillilitresPerDay(n)
    def millilitresPerDay: VolumeFlow = MillilitresPerDay(n)
    def cubicFeetPerDay: VolumeFlow = CubicFeetPerDay(n)
    def cubicFeetPerHour: VolumeFlow = CubicFeetPerHour(n)
    def cubicFeetPerMinute: VolumeFlow = CubicFeetPerMinute(n)
    def cubicFeetPerSecond: VolumeFlow = CubicFeetPerSecond(n)
    def gallonsPerDay: VolumeFlow = GallonsPerDay(n)
    def gallonsPerHour: VolumeFlow = GallonsPerHour(n)
    def gallonsPerMinute: VolumeFlow = GallonsPerMinute(n)
    def gallonsPerSecond: VolumeFlow = GallonsPerSecond(n)
  }

  given VolumeFlowNumeric: AbstractQuantityNumeric[VolumeFlow](CubicMetersPerSecond) {}
}
