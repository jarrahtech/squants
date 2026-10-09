/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2013-2015, Gary Keorkunian                                       **
**                                                                      **
\*                                                                      */

package squants

import java.util.Objects

import scala.math.BigDecimal.RoundingMode
import scala.math.BigDecimal.RoundingMode.RoundingMode

/**
 * A base class for measurable quantities, instances of which contain a value and a unit
 *
 * @author  garyKeorkunian
 * @since   0.1
 *
 */
abstract class Quantity[A <: Quantity[A]] extends Serializable with Ordered[A] { self: A =>

  /**
   * The value of the quantity given the unit
   * @return Double
   */
  def value: Double

  /**
   * The Unit of Measure the value represents
   * @return UnitOfMeasure[A]
   */
  def unit: UnitOfMeasure[A]

  /**
   * The Dimension this quantity represents
   * @return
   */
  def dimension: Dimension[A]

  /**
   * Add two like quantities
   * @param that Quantity
   * @return Quantity
   */
  infix def plus(that: A): A = unit(this.value + that.to(unit))
  def +(that: A): A = plus(that)

  /**
   * Subtract two like quantities
   * @param that Quantity
   * @return Quantity
   */
  infix def minus(that: A): A = plus(that.negate)
  def -(that: A): A = minus(that)

  /**
   * Multiply this quantity by some number
   * @param that Double
   * @return Quantity
   */
  infix def times(that: Double): A = unit(this.value * that)
  def *(that: Double): A = times(that)

  def *(that: Price[A]): Money = that * this

  /**
   * Divide this quantity by some number
   * @param that Double
   * @return Quantity
   */
  infix def divide(that: Double): A = unit(this.value / that)
  def /(that: Double): A = divide(that)

  /**
   * Divide this quantity by a like quantity
   * @param that Quantity
   * @return Double
   */
  infix def divide(that: A): Double = this.value / that.to(unit)
  def /(that: A): Double = divide(that)

  /**
   * Returns the remainder of a division by a number
   * @param that Quantity
   * @return Quantity
   */
  infix def remainder(that: Double): A = unit(this.value % that)
  def %(that: Double): A = remainder(that)

  /**
   * Returns the remainder of a division by a like quantity
   * @param that Quantity
   * @return Double
   */
  infix def remainder(that: A): Double = this.value % that.to(unit)
  def %(that: A): Double = remainder(that)

  /**
   * Returns a Pair that includes the result of divideToInteger and remainder
   * @param that Double
   * @return (Quantity, Quantity)
   */
  infix def divideAndRemainder(that: Double): (A, A) = BigDecimal(value) /% that match {
    case (q, r) => (unit(q.toDouble), unit(r.toDouble))
  }
  def /%(that: Double): (A, A) = divideAndRemainder(that)

  /**
   * Returns a Pair that includes the result of divideToInteger and remainder
   * @param that Quantity
   * @return (Double, Quantity)
   */
  infix def divideAndRemainder(that: A): (Double, A) = BigDecimal(value) /% that.to(unit) match {
    case (q, r) => (q.toDouble, unit(r.toDouble))
  }
  def /%(that: A): (Double, A) = divideAndRemainder(that)

  /**
   * Returns the negative value of this Quantity
   * @return Quantity
   */
  def negate: A = unit(-value)
  def unary_- : A = negate

  /**
   * Returns the absolute value of this Quantity
   * @return Quantity
   */
  def abs: A = unit(math.abs(value))

  /**
   * Returns the smallest (closest to negative infinity) Quantity value that is greater than or equal to the argument and is equal to a mathematical integer.
   *
   * @see java.lang.Math#ceil(double)
   * @return Quantity
   */
  def ceil: A = unit(math.ceil(value))

  /**
   * Returns the largest (closest to positive infinity) Quantity value that is less than or equal to the argument and is equal to a mathematical integer
   *
   * @see java.lang.Math#floor(double)
   * @return Quantity
   */
  def floor: A = unit(math.floor(value))

  /**
   * Returns the Quantity value that is closest in value to the argument and is equal to a mathematical integer.
   *
   * @see java.lang.Math#rint(double)
   * @return Quantity
   */
  def rint: A = unit(math.rint(value))

  /**
   * Returns the Quantity with its coefficient value rounded using scale and mode.  The unit is maintained.
   *
   * @param scale Int - scale of the value to be returned
   * @param mode RoundingMode - defaults to HALF_EVEN
   * @return Quantity
   */
  def rounded(scale: Int, mode: RoundingMode = RoundingMode.HALF_EVEN): A = unit(BigDecimal(value).setScale(scale, mode))

  /**
   * Override of equals method
   *
   * Quantities in the same unit are compared by value. Quantities in different units are both converted
   * to the primary unit, as hashCode does, so that equals is symmetric and agrees with hashCode.
   *
   * @param that must be of matching dimension and equivalent value
   * @return
   */
  override def equals(that: Any): Boolean = that match {
    case x: Quantity[_] if x.dimension == dimension =>
      val other = x.asInstanceOf[Quantity[A]]
      if (other.unit == unit) value == other.value
      else to(dimension.primaryUnit) == other.to(dimension.primaryUnit)
    case _ => false
  }

  /**
   * Override of hashCode
   *
   * @return
   */
  override def hashCode(): Int = {
    Objects.hash(dimension, Double.box(to(dimension.primaryUnit)))
  }

  /**
   * Returns boolean result of approximate equality comparison
   * @param that Quantity
   * @param tolerance Quantity
   * @return
   */
  infix def approx(that: A)(using tolerance: A): Boolean = that.within(this.plusOrMinus(tolerance))
  /** approx */
  def =~(that: A)(using tolerance: A): Boolean = approx(that)
  /** approx */
  def ≈(that: A)(using tolerance: A): Boolean = approx(that)
  /** approx */
  def ~=(that: A)(using tolerance: A): Boolean = approx(that)

  /**
   * Implements Ordered.compare
   * @param that Quantity
   * @return Int
   */
  def compare(that: A): Int = {
    val other = that.to(unit)
    if (this.value > other) 1 else if (this.value < other) -1 else 0
  }

  /**
   * Returns the max of this and that Quantity
   * @param that Quantity
   * @return Quantity
   */
  infix def max(that: A): A = if (this.value >= that.to(unit)) this else that

  /**
   * Returns the min of this and that Quantity
   * @param that Quantity
   * @return Quantity
   */
  infix def min(that: A): A = if (this.value <= that.to(unit)) this else that

  /**
   * Returns a QuantityRange representing the range for this value +- that
   * @param that Quantity
   * @return QuantityRange
   */
  infix def plusOrMinus(that: A): QuantityRange[A] = QuantityRange(this - that, this + that)
  def +-(that: A): QuantityRange[A] = plusOrMinus(that)

  /**
   * Returns a QuantityRange that goes from this to that
   * @param that Quantity
   * @return QuantityRange
   */
  infix def to(that: A): QuantityRange[A] = QuantityRange(this / 1, that)

  /**
   * Returns true if this value is within (contains) the range
   * @param range QuantityRange
   * @return Boolean
   */
  infix def within(range: QuantityRange[A]): Boolean = range.contains(self)

  /**
   * Returns true if this value is not within (contains) the range
   * @param range QuantityRange
   * @return Boolean
   */
  infix def notWithin(range: QuantityRange[A]): Boolean = !range.contains(self)

  /**
   * Returns a Double representing the quantity in terms of the supplied unit
   * {{{
   *   val d = Feet(3)
   *   (d to Inches) should be(36)
   * }}}
   * @param uom UnitOfMeasure[A]
   * @return Double
   */
  infix def to(uom: UnitOfMeasure[A]): Double = uom match {
    case u if u == this.unit => value
    case _ => uom.convertTo(this.unit.convertFrom(value))
  }

  /**
   * Returns an equivalent Quantity boxed with the supplied Unit
   * @param uom UnitOfMeasure[A]
   * @return Quantity
   */
  infix def in(uom: UnitOfMeasure[A]): A = uom match {
    case u if u == this.unit => this
    case _ => uom(uom.convertTo(this.unit.convertFrom(value)))
  }

  /**
   * Returns a string representing the quantity's value in unit
   * @return String
   */
  override def toString: String = toString(unit)

  /**
   * Returns a string representing the quantity's value in the given `unit`
   * @param uom UnitOfMeasure[A] with UnitConverter
   * @return String
   */
  infix def toString(uom: UnitOfMeasure[A]): String = s"${Platform.crossFormat(to(uom))} ${uom.symbol}"

  /**
   * Returns a string representing the quantity's value in the given `unit` in the given `format`
   * @param uom UnitOfMeasure[A] with UnitConverter
   * @param format String containing the format for the value (ie "%.3f")
   * @return String
   */
  def toString(uom: UnitOfMeasure[A], format: String): String = "%s %s".format(format.format(to(uom)), uom.symbol)

  /**
   * Returns a tuple representing the numeric value and the unit's symbol
   * @return
   */
  def toTuple: (Double, String) = (value, unit.symbol)

  /**
   * Returns a pair representing the numeric value and the uom's symbol
   * @param uom UnitOfMeasure[A]
   * @return
   */
  infix def toTuple(uom: UnitOfMeasure[A]): (Double, String) = (to(uom), uom.symbol)

  /**
   * Applies a function to the underlying value of the Quantity, returning a new Quantity in the same unit
   * @param f Double => Double function
   * @return
   */
  def map(f: Double => Double): A = unit(f(value))
}

