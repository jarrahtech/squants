/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2013-2018, Gary Keorkunian                                       **
**                                                                      **
\*                                                                      */

package squants.radio

import squants._
import scala.util.Try

/**
 * @author  Hunter Payne
 *
 * @param value Double
 */
final class Activity private (
  val value: Double, val unit: ActivityUnit)
  extends Quantity[Activity] {

  def dimension = Activity

  def /(that: AreaTime): ParticleFlux = BecquerelsPerSquareMeterSecond(
    this.toBecquerels / that.toSquareMeterSeconds)

  def toCuries: Double = to(Curies)
  def toBecquerels: Double = to(Becquerels)
  def toRutherfords: Double = to(Rutherfords)
}

object Activity extends Dimension[Activity] {
  private[radio] def apply[A](n: A, unit: ActivityUnit)(using num: Numeric[A]) = new Activity(num.toDouble(n), unit)
  def apply(value: Any): Try[Activity] = parse(value)
  def name = "Activity"
  def primaryUnit = Becquerels
  def siUnit = Becquerels
  def units: Set[UnitOfMeasure[Activity]] = Set(Becquerels, Curies, Rutherfords)
}

trait ActivityUnit
  extends UnitOfMeasure[Activity] with UnitConverter {
  def apply[A](n: A)(using num: Numeric[A]): Activity = Activity(n, this)
}

object Curies extends ActivityUnit {
  val conversionFactor: Double = 3.7 * Math.pow(10, 10)
  val symbol = "Ci"
}

object Rutherfords extends ActivityUnit {
  val conversionFactor = 1000000.0
  val symbol = "Rd"
}

object Becquerels extends ActivityUnit with PrimaryUnit with SiUnit {
  val symbol = "Bq"
}

object ActivityConversions {
  lazy val curie: Activity = Curies(1)
  lazy val rutherford: Activity = Rutherfords(1)
  lazy val becquerel: Activity = Becquerels(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def curies: Activity = Curies(n)
    def rutherfords: Activity = Rutherfords(n)
    def becquerels: Activity = Becquerels(n)
  }

  given ActivityNumeric: AbstractQuantityNumeric[Activity](Activity.primaryUnit) {}
}
