/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2013-2015, Gary Keorkunian                                       **
**                                                                      **
\*                                                                      */

package squants.information

import squants._
import squants.time.TimeIntegral
import scala.util.Try

/**
 * Represents information.
 *
 * @author Derek Morr
 * @since 0.6.0
 * @param value value in [[squants.information.Bytes]]
 */
final class Information private (val value: Double, val unit: InformationUnit)
  extends Quantity[Information]
  with TimeIntegral[DataRate] {

  def dimension = Information

  protected def timeDerived: DataRate = BytesPerSecond(toBytes)
  protected[squants] def time: Time = Seconds(1)

  def toBytes: Double = to(Bytes)
  def toKilobytes: Double = to(Kilobytes)
  def toKibibytes: Double = to(Kibibytes)
  def toMegabytes: Double = to(Megabytes)
  def toMebibytes: Double = to(Mebibytes)
  def toGigabytes: Double = to(Gigabytes)
  def toGibibytes: Double = to(Gibibytes)
  def toTerabytes: Double = to(Terabytes)
  def toTebibytes: Double = to(Tebibytes)
  def toPetabytes: Double = to(Petabytes)
  def toPebibytes: Double = to(Pebibytes)
  def toExabytes: Double = to(Exabytes)
  def toExbibytes: Double = to(Exbibytes)
  def toZettabytes: Double = to(Zettabytes)
  def toZebibytes: Double = to(Zebibytes)
  def toYottabytes: Double = to(Yottabytes)
  def toYobibytes: Double = to(Yobibytes)

  def toBits: Double = to(Bits)
  def toKilobits: Double = to(Kilobits)
  def toKibibits: Double = to(Kibibits)
  def toMegabits: Double = to(Megabits)
  def toMebibits: Double = to(Mebibits)
  def toGigabits: Double = to(Gigabits)
  def toGibibits: Double = to(Gibibits)
  def toTerabits: Double = to(Terabits)
  def toTebibits: Double = to(Tebibits)
  def toPetabits: Double = to(Petabits)
  def toPebibits: Double = to(Pebibits)
  def toExabits: Double = to(Exabits)
  def toExbibits: Double = to(Exbibits)
  def toZettabits: Double = to(Zettabits)
  def toZebibits: Double = to(Zebibits)
  def toYottabits: Double = to(Yottabits)
  def toYobibits: Double = to(Yobibits)
}

trait InformationUnit extends UnitOfMeasure[Information] with UnitConverter {
  def apply[A](n: A)(using num: Numeric[A]): Information = Information(n, this)
}

/**
 * Factory singleton for information
 */
object Information extends Dimension[Information] with BaseDimension {
  private[information] def apply[A](n: A, unit: InformationUnit)(using num: Numeric[A]) = new Information(num.toDouble(n), unit)
  def apply(value: Any): Try[Information] = parse(value)
  def name = "Information"
  def primaryUnit = Bytes
  def siUnit = Bytes
  def units: Set[UnitOfMeasure[Information]] = Set(Bytes, Kilobytes, Kibibytes, Megabytes, Mebibytes,
    Gigabytes, Gibibytes, Terabytes, Tebibytes, Petabytes, Pebibytes,
    Exabytes, Exbibytes, Zettabytes, Zebibytes, Yottabytes, Yobibytes,
    Bits, Kilobits, Kibibits, Megabits, Mebibits, Gigabits, Gibibits,
    Terabits, Tebibits, Petabits, Pebibits, Exabits, Exbibits,
    Zettabits, Zebibits, Yottabits, Yobibits)
  def dimensionSymbol = "B"
}

object Bytes extends InformationUnit with PrimaryUnit with SiBaseUnit {
  val symbol = "B"
}

object Octets extends InformationUnit {
  val conversionFactor = 1.0d
  val symbol = "o"
}

object Kilobytes extends InformationUnit {
  val conversionFactor = MetricSystem.Kilo
  val symbol = "KB"
}

object Kibibytes extends InformationUnit {
  val conversionFactor = BinarySystem.Kilo
  val symbol = "KiB"
}

object Megabytes extends InformationUnit {
  val conversionFactor = MetricSystem.Mega
  val symbol = "MB"
}

object Mebibytes extends InformationUnit {
  val conversionFactor = BinarySystem.Mega
  val symbol = "MiB"
}

object Gigabytes extends InformationUnit {
  val conversionFactor = MetricSystem.Giga
  val symbol = "GB"
}

object Gibibytes extends InformationUnit {
  val conversionFactor = BinarySystem.Giga
  val symbol = "GiB"
}

object Terabytes extends InformationUnit {
  val conversionFactor = MetricSystem.Tera
  val symbol = "TB"
}

object Tebibytes extends InformationUnit {
  val conversionFactor = BinarySystem.Tera
  val symbol = "TiB"
}

object Petabytes extends InformationUnit {
  val conversionFactor = MetricSystem.Peta
  val symbol = "PB"
}

object Pebibytes extends InformationUnit {
  val conversionFactor = BinarySystem.Peta
  val symbol = "PiB"
}

object Exabytes extends InformationUnit {
  val conversionFactor = MetricSystem.Exa
  val symbol = "EB"
}

object Exbibytes extends InformationUnit {
  val conversionFactor = BinarySystem.Exa
  val symbol = "EiB"
}

object Zettabytes extends InformationUnit {
  def conversionFactor = MetricSystem.Zetta
  def symbol = "ZB"
}

object Zebibytes extends InformationUnit {
  def conversionFactor = BinarySystem.Zetta
  def symbol = "ZiB"
}

object Yottabytes extends InformationUnit {
  def conversionFactor = MetricSystem.Yotta
  def symbol = "YB"
}

object Yobibytes extends InformationUnit {
  def conversionFactor = BinarySystem.Yotta
  def symbol = "YiB"
}

object Bits extends InformationUnit {
  def conversionFactor = 0.125d
  val symbol = "bit"
}

object Kilobits extends InformationUnit {
  val conversionFactor: Double = Bits.conversionFactor * MetricSystem.Kilo
  val symbol = "Kbit"
}

object Kibibits extends InformationUnit {
  val conversionFactor: Double = Bits.conversionFactor * BinarySystem.Kilo
  val symbol = "Kibit"
}

object Megabits extends InformationUnit {
  val conversionFactor: Double = Bits.conversionFactor * MetricSystem.Mega
  val symbol = "Mbit"
}

object Mebibits extends InformationUnit {
  val conversionFactor: Double = Bits.conversionFactor * BinarySystem.Mega
  val symbol = "Mibit"
}

object Gigabits extends InformationUnit {
  val conversionFactor: Double = Bits.conversionFactor * MetricSystem.Giga
  val symbol = "Gbit"
}

object Gibibits extends InformationUnit {
  val conversionFactor: Double = Bits.conversionFactor * BinarySystem.Giga
  val symbol = "Gibit"
}

object Terabits extends InformationUnit {
  val conversionFactor: Double = Bits.conversionFactor * MetricSystem.Tera
  val symbol = "Tbit"
}

object Tebibits extends InformationUnit {
  val conversionFactor: Double = Bits.conversionFactor * BinarySystem.Tera
  val symbol = "Tibit"
}

object Petabits extends InformationUnit {
  val conversionFactor: Double = Bits.conversionFactor * MetricSystem.Peta
  val symbol = "Pbit"
}

object Pebibits extends InformationUnit {
  val conversionFactor: Double = Bits.conversionFactor * BinarySystem.Peta
  val symbol = "Pibit"
}

object Exabits extends InformationUnit {
  val conversionFactor: Double = Bits.conversionFactor * MetricSystem.Exa
  val symbol = "Ebit"
}

object Exbibits extends InformationUnit {
  val conversionFactor: Double = Bits.conversionFactor * BinarySystem.Exa
  val symbol = "Eibit"
}

object Zettabits extends InformationUnit {
  def conversionFactor: Double = Bits.conversionFactor * MetricSystem.Zetta
  def symbol = "Zbit"
}

object Zebibits extends InformationUnit {
  def conversionFactor: Double = Bits.conversionFactor * BinarySystem.Zetta
  def symbol = "Zibit"
}

object Yottabits extends InformationUnit {
  def conversionFactor: Double = Bits.conversionFactor * MetricSystem.Yotta
  def symbol = "Ybit"
}

object Yobibits extends InformationUnit {
  def conversionFactor: Double = Bits.conversionFactor * BinarySystem.Yotta
  def symbol = "Yibit"
}

object InformationConversions {
  lazy val byte: Information = Bytes(1)
  lazy val kilobyte: Information = Kilobytes(1)
  lazy val kibibyte: Information = Kibibytes(1)
  lazy val megabyte: Information = Megabytes(1)
  lazy val mebibyte: Information = Mebibytes(1)
  lazy val gigabyte: Information = Gigabytes(1)
  lazy val gibibyte: Information = Gibibytes(1)
  lazy val terabyte: Information = Terabytes(1)
  lazy val tebibyte: Information = Tebibytes(1)
  lazy val petabyte: Information = Petabytes(1)
  lazy val pebibyte: Information = Pebibytes(1)
  lazy val exabyte: Information = Exabytes(1)
  lazy val exbibyte: Information = Exbibytes(1)
  lazy val zettabyte: Information = Zettabytes(1)
  lazy val zebibyte: Information = Zebibytes(1)
  lazy val yottabyte: Information = Yottabytes(1)
  lazy val yobibyte: Information = Yobibytes(1)

  lazy val bit: Information = Bits(1)
  lazy val kilobit: Information = Kilobits(1)
  lazy val kibibit: Information = Kibibits(1)
  lazy val megabit: Information = Megabits(1)
  lazy val mebibit: Information = Mebibits(1)
  lazy val gigabit: Information = Gigabits(1)
  lazy val gibibit: Information = Gibibits(1)
  lazy val terabit: Information = Terabits(1)
  lazy val tebibit: Information = Tebibits(1)
  lazy val petabit: Information = Petabits(1)
  lazy val pebibit: Information = Pebibits(1)
  lazy val exabit: Information = Exabits(1)
  lazy val exbibit: Information = Exbibits(1)
  lazy val zettabit: Information = Zettabits(1)
  lazy val zebibit: Information = Zebibits(1)
  lazy val yottabit: Information = Yottabits(1)
  lazy val yobibit: Information = Yobibits(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def bytes: Information = Bytes(n)
    def kb: Information = Kilobytes(n)
    def kilobytes: Information = Kilobytes(n)
    def mb: Information = Megabytes(n)
    def megabytes: Information = Megabytes(n)
    def gb: Information = Gigabytes(n)
    def gigabytes: Information = Gigabytes(n)
    def tb: Information = Terabytes(n)
    def terabytes: Information = Terabytes(n)
    def pb: Information = Petabytes(n)
    def petabytes: Information = Petabytes(n)
    def eb: Information = Exabytes(n)
    def exabytes: Information = Exabytes(n)
    def zb: Information = Zettabytes(n)
    def zettabytes: Information = Zettabytes(n)
    def yb: Information = Yottabytes(n)
    def yottabytes: Information = Yottabytes(n)

    def kib: Information = Kibibytes(n)
    def kibibytes: Information = Kibibytes(n)
    def mib: Information = Mebibytes(n)
    def mebibytes: Information = Mebibytes(n)
    def gib: Information = Gibibytes(n)
    def gibibytes: Information = Gibibytes(n)
    def tib: Information = Tebibytes(n)
    def tebibytes: Information = Tebibytes(n)
    def pib: Information = Pebibytes(n)
    def pebibytes: Information = Pebibytes(n)
    def eib: Information = Exbibytes(n)
    def exbibytes: Information = Exbibytes(n)
    def zib: Information = Zebibytes(n)
    def zebibytes: Information = Zebibytes(n)
    def yib: Information = Yobibytes(n)
    def yobibytes: Information = Yobibytes(n)

    def bits: Information = Bits(n)
    def kilobits: Information = Kilobits(n)
    def megabits: Information = Megabits(n)
    def gigabits: Information = Gigabits(n)
    def terabits: Information = Terabits(n)
    def petabits: Information = Petabits(n)
    def exabits: Information = Exabits(n)
    def zettabits: Information = Zettabits(n)
    def yottabits: Information = Yottabits(n)

    def kibibits: Information = Kibibits(n)
    def mebibits: Information = Mebibits(n)
    def gibibits: Information = Gibibits(n)
    def tebibits: Information = Tebibits(n)
    def pebibits: Information = Pebibits(n)
    def exbibits: Information = Exbibits(n)
    def zebibits: Information = Zebibits(n)
    def yobibits: Information = Yobibits(n)
  }

  extension (s: String) {
    def toInformation: Try[Information] = Information(s)
  }

  given InformationNumeric: AbstractQuantityNumeric[Information](Information.primaryUnit) {}
}
