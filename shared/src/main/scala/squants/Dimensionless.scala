/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2013-2015, Gary Keorkunian                                       **
**                                                                      **
\*                                                                      */

package squants

import squants.time.{ Frequency, Hertz, TimeIntegral }
import scala.util.Try

/**
 * Represents a quantity of some thing for which there is no dimension.
 *
 * This may be used to represent counts or other discrete amounts of everyday life,
 * but may also represent ratios between like quantities where the units have cancelled out.
 *
 * @author  garyKeorkunian
 * @since   0.1
 *
 * @param value Double the amount
 */
final class Dimensionless private (val value: Double, val unit: DimensionlessUnit)
  extends Quantity[Dimensionless]
  with TimeIntegral[Frequency] {

  def dimension = Dimensionless

  protected def timeDerived: Frequency = Hertz(toEach)
  protected[squants] def time: Time = Seconds(1)

  def *(that: Dimensionless): Dimensionless = Each(toEach * that.toEach)
  def *(that: Quantity[?]) = that * toEach

  def +(that: Double): Dimensionless = this + Each(that)

  def toPercent: Double = to(Percent)
  def toEach: Double = to(Each)
  def toDozen: Double = to(Dozen)
  def toScore: Double = to(Score)
  def toGross: Double = to(Gross)
}

/**
 * Factory singleton for [[squants.Dimensionless]]
 */
object Dimensionless extends Dimension[Dimensionless] {
  def apply[A](n: A, unit: DimensionlessUnit)(using num: Numeric[A]) = new Dimensionless(num.toDouble(n), unit)
  def apply(value: Any): Try[Dimensionless] = parse(value)
  def name = "Dimensionless"
  def primaryUnit = Each
  def siUnit = Each
  def units: Set[UnitOfMeasure[Dimensionless]] = Set(Each, Percent, Dozen, Score, Gross)
}

/**
 * Base trait for units of [[squants.Dimensionless]]
 *
 * The DimensionlessUnit is a useful paradox
 */
trait DimensionlessUnit extends UnitOfMeasure[Dimensionless] with UnitConverter {
  def apply[A](n: A)(using num: Numeric[A]): Dimensionless = Dimensionless(n, this)
}

/**
 * Represents a unit of singles
 */
object Each extends DimensionlessUnit with PrimaryUnit with SiUnit {
  val symbol = "ea"
}

/**
 * Represents a number of hundredths (0.01)
 */
object Percent extends DimensionlessUnit {
  val conversionFactor = 1e-2
  val symbol = "%"
}

/**
 * Represents a unit of dozen (12)
 */
object Dozen extends DimensionlessUnit {
  val conversionFactor = 12d
  val symbol = "dz"
}

/**
 * Represents a unit of scores (20)
 */
object Score extends DimensionlessUnit {
  val conversionFactor = 20d
  val symbol = "score"
}

/**
 * Represents a unit of gross (144)
 */
object Gross extends DimensionlessUnit {
  val conversionFactor = 144d
  val symbol = "gr"
}

object DimensionlessConversions {
  lazy val percent: Dimensionless = Percent(1)
  lazy val each: Dimensionless = Each(1)
  lazy val dozen: Dimensionless = Dozen(1)
  lazy val score: Dimensionless = Score(1)
  lazy val gross: Dimensionless = Gross(1)
  lazy val hundred: Dimensionless = Each(1e2)
  lazy val thousand: Dimensionless = Each(1e3)
  lazy val million: Dimensionless = Each(1e6)


  extension [A](n: A)(using num: Numeric[A]) {
    def percent: Dimensionless = Percent(n)
    def each: Dimensionless = Each(n)
    def ea: Dimensionless = Each(n)
    def dozen: Dimensionless = Dozen(n)
    def dz: Dimensionless = Dozen(n)
    def score: Dimensionless = Score(n)
    def gross: Dimensionless = Gross(n)
    def gr: Dimensionless = Gross(n)
    def hundred: Dimensionless = Each(num.toDouble(n) * 1e2)
    def thousand: Dimensionless = Each(num.toDouble(n) * 1e3)
    def million: Dimensionless = Each(num.toDouble(n) * 1e6)
  }
  /**
   * Provides an implicit conversion from Dimensionless to Double, allowing a Dimensionless value
   * to be used anywhere a Double (or similar primitive) is required
   *
   * @param d Dimensionless
   * @return
   */
  given dimensionlessToDouble: Conversion[Dimensionless, Double] = _.toEach

  given DimensionlessNumeric: AbstractQuantityNumeric[Dimensionless](Dimensionless.primaryUnit) {
    /**
     * Dimensionless quantities support the times operation.
     * This method overrides the default [[squants.AbstractQuantityNumeric.times]] which throws an exception
     *
     * @param x Dimensionless
     * @param y Dimensionless
     * @return
     */
    override def times(x: Dimensionless, y: Dimensionless): Dimensionless = x * y
  }
}
