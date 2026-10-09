/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2013-2015, Gary Keorkunian                                       **
**                                                                      **
\*                                                                      */

package squants

/**
 * A Unit of Measure is used to define the scale of a quantity measurement
 *
 * Each Quantity Dimension must include at least one Unit of Measure, and one and only one Primary.
 * Other units of measure are defined with conversionFactors relative to the Primary.
 *
 * @author  garyKeorkunian
 * @since   0.1
 *
 * @tparam A The type of Quantity being measured
 */
trait UnitOfMeasure[A <: Quantity[A]] extends PrimaryConversion with Serializable {
  /**
   * Factory method for creating instances of a Quantity in this UnitOfMeasure
   * @param n N - the Quantity's value in terms of this UnitOfMeasure
   * @return
   */
  def apply[N](n: N)(using num: Numeric[N]): A

  /**
   * Extractor method for getting the Numeric value of a Quantity in this UnitOfMeasure
   * @param q A - The Quantity being matched
   * @return
   */
  def unapply(q: A): Some[Double] = Some(q.to(this))

  /**
   * Symbol used when representing Quantities in this UnitOfMeasure
   * @return
   */
  def symbol: String

  /**
   * Defines a signature for converting a quantity from this UOM to the Value UOM
   * @return
   */
  protected def converterFrom: Double => Double

  /**
   * Defines a signature for converting a quantity to this UOM from the Value UOM
   * @return
   */
  protected def converterTo: Double => Double

  /**
   * Applies the converterTo method to a value
   * @param n N value in terms of the ValueUnit
   * @param num Numeric[N]
   * @tparam N Type
   * @return
   */
  final def convertTo[N](n: N)(using num: Numeric[N]): Double = converterTo(num.toDouble(n))

  /**
   * Applies the converterFrom method to a value
   *
   * @param n N value in terms of this Unit
   * @param num Numeric[N]
   * @tparam N Type
   * @return
   */
  final def convertFrom[N](n: N)(using num: Numeric[N]): Double = converterFrom(num.toDouble(n))

  private[squants] def fromPrimary(value: Double): Double = converterTo(value)
  private[squants] def toPrimary(value: Double): Double = converterFrom(value)
}

/**
 * The conversions Quantity itself uses, to and from the Quantity's [[squants.PrimaryUnit]].
 *
 * They take the Double directly, where convertTo and convertFrom box it to pass it through Numeric, and a
 * [[squants.UnitConverter]] implements them as plain arithmetic, with no function value built per conversion.
 */
trait PrimaryConversion {
  private[squants] def fromPrimary(value: Double): Double
  private[squants] def toPrimary(value: Double): Double
}

/**
 * A Unit of Measure that require a simple multiplier for converting to and from the underlying value's unit
 */
trait UnitConverter extends PrimaryConversion { uom: UnitOfMeasure[?] =>

  /**
   * Defines a multiplier value relative to the Quantity's [[squants.PrimaryUnit]]
   *
   * @return
   */
  protected def conversionFactor: Double

  /**
   * Converts to this unit as a simple quotient of the value and the multiplier
   */
  override private[squants] def fromPrimary(value: Double): Double = value / conversionFactor

  /**
   * Converts from this unit as a simple product of the value and the multiplier
   */
  override private[squants] def toPrimary(value: Double): Double = value * conversionFactor

  // Final so that they cannot drift from the two methods above, which are what Quantity converts through.
  // A unit that converts some other way extends UnitOfMeasure without this trait.
  protected final def converterTo: Double => Double = fromPrimary(_)
  protected final def converterFrom: Double => Double = toPrimary(_)
}

/**
 * Identifies the Unit of Measure with a conversionFactor of 1.0.
 *
 * It is used as the intermediary unit during conversions
 *
 * Each Quantity should have one and only one ValueUnit
 */
trait PrimaryUnit extends UnitConverter { uom: UnitOfMeasure[?] =>

  /**
   * Conversion to and from the primary unit just returns the underlying value
   */
  override private[squants] final def fromPrimary(value: Double): Double = value
  override private[squants] final def toPrimary(value: Double): Double = value

  /**
   * Value unit multiplier is always equal to 1
   */
  final val conversionFactor = 1d
}

/**
 * A marker trait identifying SI Units
 */
trait SiUnit

/**
 * A marker trait identifying SI Base Units
 */
trait SiBaseUnit extends SiUnit
