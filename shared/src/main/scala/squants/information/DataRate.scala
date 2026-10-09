package squants.information

import squants._
import squants.time.TimeDerivative
import scala.util.Try

/**
 * Represents a rate of transfer of information
 */
final class DataRate private (val value: Double, val unit: DataRateUnit)
  extends Quantity[DataRate]
  with TimeDerivative[Information] {

  def dimension = DataRate
  protected[squants] def timeIntegrated: Information = Bytes(toBytesPerSecond)
  protected[squants] def time: Time = Seconds(1)

  def toBytesPerSecond: Double = to(BytesPerSecond)
  def toKilobytesPerSecond: Double = to(KilobytesPerSecond)
  def toMegabytesPerSecond: Double = to(MegabytesPerSecond)
  def toGigabytesPerSecond: Double = to(GigabytesPerSecond)
  def toTerabytesPerSecond: Double = to(TerabytesPerSecond)
  def toPetabytesPerSecond: Double = to(PetabytesPerSecond)
  def toExabytesPerSecond: Double = to(ExabytesPerSecond)
  def toZettabytesPerSecond: Double = to(ZettabytesPerSecond)
  def toYottabytesPerSecond: Double = to(YottabytesPerSecond)

  def toKibibytesPerSecond: Double = to(KibibytesPerSecond)
  def toMebibytesPerSecond: Double = to(MebibytesPerSecond)
  def toGibibytesPerSecond: Double = to(GibibytesPerSecond)
  def toTebibytesPerSecond: Double = to(TebibytesPerSecond)
  def toPebibytesPerSecond: Double = to(PebibytesPerSecond)
  def toExbibytesPerSecond: Double = to(ExbibytesPerSecond)
  def toZebibytesPerSecond: Double = to(ZebibytesPerSecond)
  def toYobibytesPerSecond: Double = to(YobibytesPerSecond)

  def toBitsPerSecond: Double = to(BitsPerSecond)
  def toKilobitsPerSecond: Double = to(KilobitsPerSecond)
  def toMegabitsPerSecond: Double = to(MegabitsPerSecond)
  def toGigabitsPerSecond: Double = to(GigabitsPerSecond)
  def toTerabitsPerSecond: Double = to(TerabitsPerSecond)
  def toPetabitsPerSecond: Double = to(PetabitsPerSecond)
  def toExabitsPerSecond: Double = to(ExabitsPerSecond)
  def toZettabitsPerSecond: Double = to(ZettabitsPerSecond)
  def toYottabitsPerSecond: Double = to(YottabitsPerSecond)

  def toKibibitsPerSecond: Double = to(KibibitsPerSecond)
  def toMebibitsPerSecond: Double = to(MebibitsPerSecond)
  def toGibibitsPerSecond: Double = to(GibibitsPerSecond)
  def toTebibitsPerSecond: Double = to(TebibitsPerSecond)
  def toPebibitsPerSecond: Double = to(PebibitsPerSecond)
  def toExbibitsPerSecond: Double = to(ExbibitsPerSecond)
  def toZebibitsPerSecond: Double = to(ZebibitsPerSecond)
  def toYobibitsPerSecond: Double = to(YobibitsPerSecond)
}

object DataRate extends Dimension[DataRate] {
  private[information] def apply[A](n: A, unit: DataRateUnit)(using num: Numeric[A]) =
    new DataRate(num.toDouble(n), unit)

  def apply(i: Information, t: Time): DataRate = BytesPerSecond(i.toBytes / t.toSeconds)
  def apply(value: Any): Try[DataRate] = parse(value)
  def name = "DataRate"
  def primaryUnit = BytesPerSecond
  def siUnit = BytesPerSecond
  def units: Set[UnitOfMeasure[DataRate]] = Set(BytesPerSecond, KilobytesPerSecond, KibibytesPerSecond, MegabytesPerSecond, MebibytesPerSecond,
    GigabytesPerSecond, GibibytesPerSecond, TerabytesPerSecond, TebibytesPerSecond,
    PetabytesPerSecond, PebibytesPerSecond, ExabytesPerSecond, ExbibytesPerSecond,
    ZettabytesPerSecond, ZebibytesPerSecond, YottabytesPerSecond, YobibytesPerSecond,
    BitsPerSecond, KilobitsPerSecond, KibibitsPerSecond, MegabitsPerSecond, MebibitsPerSecond,
    GigabitsPerSecond, GibibitsPerSecond, TerabitsPerSecond, TebibitsPerSecond,
    PetabitsPerSecond, PebibitsPerSecond, ExabitsPerSecond, ExbibitsPerSecond,
    ZettabitsPerSecond, ZebibitsPerSecond, YottabitsPerSecond, YobibitsPerSecond)
}

trait DataRateUnit extends UnitOfMeasure[DataRate] with UnitConverter {
  def apply[A](n: A)(using num: Numeric[A]): DataRate = DataRate(n, this)
}

object BytesPerSecond extends DataRateUnit with PrimaryUnit with SiUnit {
  val symbol = "B/s"
}

object KilobytesPerSecond extends DataRateUnit {
  val symbol = "KB/s"
  val conversionFactor = Kilobytes.conversionFactor
}

object KibibytesPerSecond extends DataRateUnit {
  val symbol = "KiB/s"
  val conversionFactor = Kibibytes.conversionFactor
}

object MegabytesPerSecond extends DataRateUnit {
  val symbol = "MB/s"
  val conversionFactor = Megabytes.conversionFactor
}

object MebibytesPerSecond extends DataRateUnit {
  val symbol = "MiB/s"
  val conversionFactor = Mebibytes.conversionFactor
}

object GigabytesPerSecond extends DataRateUnit {
  val symbol = "GB/s"
  val conversionFactor = Gigabytes.conversionFactor
}

object GibibytesPerSecond extends DataRateUnit {
  val symbol = "GiB/s"
  val conversionFactor = Gibibytes.conversionFactor
}

object TerabytesPerSecond extends DataRateUnit {
  val symbol = "TB/s"
  val conversionFactor = Terabytes.conversionFactor
}

object TebibytesPerSecond extends DataRateUnit {
  val symbol = "TiB/s"
  val conversionFactor = Tebibytes.conversionFactor
}

object PetabytesPerSecond extends DataRateUnit {
  val symbol = "PB/s"
  val conversionFactor = Petabytes.conversionFactor
}

object PebibytesPerSecond extends DataRateUnit {
  val symbol = "PiB/s"
  val conversionFactor = Pebibytes.conversionFactor
}

object ExabytesPerSecond extends DataRateUnit {
  val symbol = "EB/s"
  val conversionFactor = Exabytes.conversionFactor
}

object ExbibytesPerSecond extends DataRateUnit {
  val symbol = "EiB/s"
  val conversionFactor = Exbibytes.conversionFactor
}

object ZettabytesPerSecond extends DataRateUnit {
  val symbol = "ZB/s"
  val conversionFactor = Zettabytes.conversionFactor
}

object ZebibytesPerSecond extends DataRateUnit {
  val symbol = "ZiB/s"
  val conversionFactor = Zebibytes.conversionFactor
}

object YottabytesPerSecond extends DataRateUnit {
  val symbol = "YB/s"
  val conversionFactor = Yottabytes.conversionFactor
}

object YobibytesPerSecond extends DataRateUnit {
  val symbol = "YiB/s"
  val conversionFactor = Yobibytes.conversionFactor
}

object BitsPerSecond extends DataRateUnit {
  val symbol = "bps"
  val conversionFactor = Bits.conversionFactor
}

object KilobitsPerSecond extends DataRateUnit {
  val symbol = "Kbps"
  val conversionFactor: Double = BitsPerSecond.conversionFactor * Kilobytes.conversionFactor
}

object KibibitsPerSecond extends DataRateUnit {
  val symbol = "Kibps"
  val conversionFactor: Double = BitsPerSecond.conversionFactor * Kibibytes.conversionFactor
}

object MegabitsPerSecond extends DataRateUnit {
  val symbol = "Mbps"
  val conversionFactor: Double = BitsPerSecond.conversionFactor * Megabytes.conversionFactor
}

object MebibitsPerSecond extends DataRateUnit {
  val symbol = "Mibps"
  val conversionFactor: Double = BitsPerSecond.conversionFactor * Mebibytes.conversionFactor
}

object GigabitsPerSecond extends DataRateUnit {
  val symbol = "Gbps"
  val conversionFactor: Double = BitsPerSecond.conversionFactor * Gigabytes.conversionFactor
}

object GibibitsPerSecond extends DataRateUnit {
  val symbol = "Gibps"
  val conversionFactor: Double = BitsPerSecond.conversionFactor * Gibibytes.conversionFactor
}

object TerabitsPerSecond extends DataRateUnit {
  val symbol = "Tbps"
  val conversionFactor: Double = BitsPerSecond.conversionFactor * Terabytes.conversionFactor
}

object TebibitsPerSecond extends DataRateUnit {
  val symbol = "Tibps"
  val conversionFactor: Double = BitsPerSecond.conversionFactor * Tebibytes.conversionFactor
}

object PetabitsPerSecond extends DataRateUnit {
  val symbol = "Pbps"
  val conversionFactor: Double = BitsPerSecond.conversionFactor * Petabytes.conversionFactor
}

object PebibitsPerSecond extends DataRateUnit {
  val symbol = "Pibps"
  val conversionFactor: Double = BitsPerSecond.conversionFactor * Pebibytes.conversionFactor
}

object ExabitsPerSecond extends DataRateUnit {
  val symbol = "Ebps"
  val conversionFactor: Double = BitsPerSecond.conversionFactor * Exabytes.conversionFactor
}

object ExbibitsPerSecond extends DataRateUnit {
  val symbol = "Eibps"
  val conversionFactor: Double = BitsPerSecond.conversionFactor * Exbibytes.conversionFactor
}

object ZettabitsPerSecond extends DataRateUnit {
  val symbol = "Zbps"
  val conversionFactor: Double = BitsPerSecond.conversionFactor * Zettabytes.conversionFactor
}

object ZebibitsPerSecond extends DataRateUnit {
  val symbol = "Zibps"
  val conversionFactor: Double = BitsPerSecond.conversionFactor * Zebibytes.conversionFactor
}

object YottabitsPerSecond extends DataRateUnit {
  val symbol = "Ybps"
  val conversionFactor: Double = BitsPerSecond.conversionFactor * Yottabytes.conversionFactor
}

object YobibitsPerSecond extends DataRateUnit {
  val symbol = "Yibps"
  val conversionFactor: Double = BitsPerSecond.conversionFactor * Yobibytes.conversionFactor
}

object DataRateConversions {
  lazy val bytesPerSecond: DataRate = BytesPerSecond(1)
  lazy val kilobytesPerSecond: DataRate = KilobytesPerSecond(1)
  lazy val kibibytesPerSecond: DataRate = KibibytesPerSecond(1)
  lazy val megabytesPerSecond: DataRate = MegabytesPerSecond(1)
  lazy val mebibytesPerSecond: DataRate = MebibytesPerSecond(1)
  lazy val gigabytesPerSecond: DataRate = GigabytesPerSecond(1)
  lazy val gibibytesPerSecond: DataRate = GibibytesPerSecond(1)
  lazy val terabytesPerSecond: DataRate = TerabytesPerSecond(1)
  lazy val tebibytesPerSecond: DataRate = TebibytesPerSecond(1)
  lazy val petabytesPerSecond: DataRate = PetabytesPerSecond(1)
  lazy val pebibytesPerSecond: DataRate = PebibytesPerSecond(1)
  lazy val exabytesPerSecond: DataRate = ExabytesPerSecond(1)
  lazy val exbibytesPerSecond: DataRate = ExbibytesPerSecond(1)
  lazy val zettabytesPerSecond: DataRate = ZettabytesPerSecond(1)
  lazy val zebibytesPerSecond: DataRate = ZebibytesPerSecond(1)
  lazy val yottabytesPerSecond: DataRate = YottabytesPerSecond(1)
  lazy val yobibytesPerSecond: DataRate = YobibytesPerSecond(1)

  lazy val bitsPerSecond: DataRate = BitsPerSecond(1)
  lazy val kilobitsPerSecond: DataRate = KilobitsPerSecond(1)
  lazy val kibibitsPerSecond: DataRate = KibibitsPerSecond(1)
  lazy val megabitsPerSecond: DataRate = MegabitsPerSecond(1)
  lazy val mebibitsPerSecond: DataRate = MebibitsPerSecond(1)
  lazy val gigabitsPerSecond: DataRate = GigabitsPerSecond(1)
  lazy val gibibitsPerSecond: DataRate = GibibitsPerSecond(1)
  lazy val terabitsPerSecond: DataRate = TerabitsPerSecond(1)
  lazy val tebibitsPerSecond: DataRate = TebibitsPerSecond(1)
  lazy val petabitsPerSecond: DataRate = PetabitsPerSecond(1)
  lazy val pebibitsPerSecond: DataRate = PebibitsPerSecond(1)
  lazy val exabitsPerSecond: DataRate = ExabitsPerSecond(1)
  lazy val exbibitsPerSecond: DataRate = ExbibitsPerSecond(1)
  lazy val zettabitsPerSecond: DataRate = ZettabitsPerSecond(1)
  lazy val zebibitsPerSecond: DataRate = ZebibitsPerSecond(1)
  lazy val yottabitsPerSecond: DataRate = YottabitsPerSecond(1)
  lazy val yobibitsPerSecond: DataRate = YobibitsPerSecond(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def bytesPerSecond: DataRate = BytesPerSecond(n)
    def kilobytesPerSecond: DataRate = KilobytesPerSecond(n)
    def kibibytesPerSecond: DataRate = KibibytesPerSecond(n)
    def megabytesPerSecond: DataRate = MegabytesPerSecond(n)
    def mebibytesPerSecond: DataRate = MebibytesPerSecond(n)
    def gigabytesPerSecond: DataRate = GigabytesPerSecond(n)
    def gibibytesPerSecond: DataRate = GibibytesPerSecond(n)
    def terabytesPerSecond: DataRate = TerabytesPerSecond(n)
    def tebibytesPerSecond: DataRate = TebibytesPerSecond(n)
    def petabytesPerSecond: DataRate = PetabytesPerSecond(n)
    def pebibytesPerSecond: DataRate = PebibytesPerSecond(n)
    def exabytesPerSecond: DataRate = ExabytesPerSecond(n)
    def exbibytesPerSecond: DataRate = ExbibytesPerSecond(n)
    def zettabytesPerSecond: DataRate = ZettabytesPerSecond(n)
    def zebibytesPerSecond: DataRate = ZebibytesPerSecond(n)
    def yottabytesPerSecond: DataRate = YottabytesPerSecond(n)
    def yobibytesPerSecond: DataRate = YobibytesPerSecond(n)

    def bitsPerSecond: DataRate = BitsPerSecond(n)
    def kilobitsPerSecond: DataRate = KilobitsPerSecond(n)
    def kibibitsPerSecond: DataRate = KibibitsPerSecond(n)
    def megabitsPerSecond: DataRate = MegabitsPerSecond(n)
    def mebibitsPerSecond: DataRate = MebibitsPerSecond(n)
    def gigabitsPerSecond: DataRate = GigabitsPerSecond(n)
    def gibibitsPerSecond: DataRate = GibibitsPerSecond(n)
    def terabitsPerSecond: DataRate = TerabitsPerSecond(n)
    def tebibitsPerSecond: DataRate = TebibitsPerSecond(n)
    def petabitsPerSecond: DataRate = PetabitsPerSecond(n)
    def pebibitsPerSecond: DataRate = PebibitsPerSecond(n)
    def exabitsPerSecond: DataRate = ExabitsPerSecond(n)
    def exbibitsPerSecond: DataRate = ExbibitsPerSecond(n)
    def zettabitsPerSecond: DataRate = ZettabitsPerSecond(n)
    def zebibitsPerSecond: DataRate = ZebibitsPerSecond(n)
    def yottabitsPerSecond: DataRate = YottabitsPerSecond(n)
    def yobibitsPerSecond: DataRate = YobibitsPerSecond(n)
  }

  given DataRateNumeric: AbstractQuantityNumeric[DataRate](DataRate.primaryUnit) {}
}
