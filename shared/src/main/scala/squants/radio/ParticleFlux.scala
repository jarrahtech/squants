/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2013-2018, Gary Keorkunian                                       **
**                                                                      **
\*                                                                      */

package squants.radio

import squants._
import squants.time.Hours
import scala.util.Try

/**
 * @author  Hunter Payne
 *
 * @param value Double
 */
final class ParticleFlux private (
  val value: Double, val unit: ParticleFluxUnit)
  extends Quantity[ParticleFlux] {

  def dimension = ParticleFlux

  def *(that: AreaTime): Activity =
    Becquerels(
      this.toBecquerelsPerSquareMeterSecond * that.toSquareMeterSeconds)
  def *(that: Energy): Irradiance = WattsPerSquareMeter(
    Hours(1).toSeconds * that.toWattHours *
      this.toBecquerelsPerSquareMeterSecond)

  def toBecquerelsPerSquareMeterSecond: Double = to(BecquerelsPerSquareMeterSecond)
  def toBecquerelsPerSquareCentimeterSecond: Double =
    to(BecquerelsPerSquareCentimeterSecond)
}

object ParticleFlux extends Dimension[ParticleFlux] {
  private[radio] def apply[A](n: A, unit: ParticleFluxUnit)(using num: Numeric[A]) = new ParticleFlux(num.toDouble(n), unit)
  def apply(value: Any): Try[ParticleFlux] = parse(value)
  def name = "ParticleFlux"
  def primaryUnit = BecquerelsPerSquareMeterSecond
  def siUnit = BecquerelsPerSquareMeterSecond
  def units: Set[UnitOfMeasure[ParticleFlux]] =
    Set(BecquerelsPerSquareMeterSecond, BecquerelsPerSquareCentimeterSecond)
}

trait ParticleFluxUnit
  extends UnitOfMeasure[ParticleFlux] with UnitConverter {
  def apply[A](n: A)(using num: Numeric[A]): ParticleFlux = ParticleFlux(n, this)
}

object BecquerelsPerSquareCentimeterSecond extends ParticleFluxUnit {
  val conversionFactor = 10000.0 //0.0001
  val symbol: String = Becquerels.symbol + "/cm²‧s"
}

object BecquerelsPerSquareMeterSecond
  extends ParticleFluxUnit with PrimaryUnit with SiUnit {
  val symbol: String = Becquerels.symbol + "/m²‧s"
}

object ParticleFluxConversions {
  lazy val becquerelPerSquareMeterSecond: ParticleFlux = BecquerelsPerSquareMeterSecond(1)
  lazy val becquerelPerSquareCentimeterSecond: ParticleFlux =
    BecquerelsPerSquareCentimeterSecond(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def becquerelsPerSquareMeterSecond: ParticleFlux = BecquerelsPerSquareMeterSecond(n)
    def becquerelsPerSquareCentimeterSecond: ParticleFlux =
      BecquerelsPerSquareCentimeterSecond(n)
  }

  given ParticleFluxNumeric: AbstractQuantityNumeric[ParticleFlux](ParticleFlux.primaryUnit) {}
}
