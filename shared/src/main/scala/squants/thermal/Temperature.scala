/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2013-2015, Gary Keorkunian                                       **
**                                                                      **
\*                                                                      */

package squants.thermal

import squants._
import squants.Platform.crossFormat
import squants.energy.Joules
import squants.radio.{Irradiance, WattsPerSquareMeter}
import scala.util.{ Failure, Success, Try }

/**
 * Represents a quantity of temperature
 *
 * Temperatures are somewhat unique in the world of quantities for a couple of reasons.
 *
 * First, different units (scales) have different "zero" values.  This means that these scales
 * are not simple multiples of the others.  There is a "zero offset" that must be applied to conversions
 * from one scale to another.
 *
 * Second, temperatures are often quoted as though they were quantities, when in fact they are just points
 * on a scale.  Similar to a mile marker on a highway, the quantity represented is the number degrees (miles)
 * from a specific "zero" value on the scale.
 *
 * In fact an absolute quantity of thermodynamic temperature should be measured from absolute zero.
 * Thus, Kelvin, is the SI Base unit for temperature.
 *
 * The other scales supported here, Celsius and Fahrenheit, are known as empirical scales.
 * Of course, these scales set their respective zero values well above absolute zero.
 * This is done to provide a granular and reasonably sized ranges of values for dealing with everyday temperatures.
 *
 * This library supports another absolute scale, the Rankine scale. Rankine sets its zero at absolute zero,
 * but degrees are measure in Fahrenheit (as opposed to Celsius, as the Kelvin scale uses).
 *
 * In consideration of these more unique scale conversions, two conversion types are supported: Degrees and Scale.
 *
 * Scale based conversions DO adjust for the zero offset.
 * Thus 5 degrees C is the same as 41 degrees F on the thermometer.
 *
 * Degrees based conversions DO NOT adjust for the zero point.
 * Thus 5 degrees C|K is the same amount of temperature as 9 degrees F|R.
 *
 * When creating a temperature it is not important to consider these differences.
 * It is also irrelevant when performing operation on temperatures in the same scale.
 * However, when performing operations on two temperatures of different scales these factors do become important.
 *
 * The Quantity.to(unit) and Quantity.in(unit) methods are overridden to use Scale conversions for convenience
 *
 * The Ordered.compare method is implemented to use Scale conversions
 *
 * The Quantity.plus and Quantity.minus methods are implemented to treat right operands as Quantity of Degrees and not a scale Temperature.
 * Operands that differ in scale will use Degree conversions.
 * This supports mixed scale expressions:
 *
 * val temp = Fahrenheit(100) - Celsius(5) // returns Fahrenheit(91)
 *
 * This also supports declaring temperature ranges using typical nomenclature:
 *
 * val tempRange = 65.F +- 5.C // returns QuantityRange(56.0°F,74.0°F)
 *
 * The toDegrees(unit) methods are implemented to use Degree conversions.
 *
 * Multiplying or dividing by a number (`Celsius(20) / 2`) works on the value in the temperature's own scale,
 * that is, it treats the temperature as a number of degrees: `Celsius(20) / 2` is `Celsius(10)`.
 *
 * Dividing one temperature by another (`/`, `%` and `/%`) is only supported when both are on an absolute scale
 * (Kelvin or Rankine, in any mix), where the answer is the same whether the operands are read as temperatures or as
 * numbers of degrees. On Celsius or Fahrenheit the two readings differ (20°C / 10°C is 1.035 as temperatures and 2 as
 * degrees), so the operation throws an UnsupportedOperationException instead of choosing. Say which is meant:
 *
 * a.toKelvinScale / b.toKelvinScale     // ratio of two temperatures
 * a.toKelvinDegrees / b.toKelvinDegrees // ratio of two temperature differences
 *
 * @author  garyKeorkunian
 * @since   0.1
 *
 * @param value the value of the temperature
 */

final class Temperature private (val value: Double, val unit: TemperatureScale)
  extends Quantity[Temperature] {

  def dimension = Temperature

  override infix def plus(that: Temperature): Temperature = Temperature(this.value + that.convert(unit, withOffset = false).value, unit)
  override infix def minus(that: Temperature): Temperature = Temperature(this.value - that.convert(unit, withOffset = false).value, unit)

  /**
   * The ratio of two temperatures, which must both be on an absolute scale
   * @throws scala.UnsupportedOperationException if either temperature is in Celsius or Fahrenheit
   */
  override infix def divide(that: Temperature): Double = {
    requireAbsolute("divide", that)
    super.divide(that)
  }

  /**
   * The remainder of dividing two temperatures, which must both be on an absolute scale
   * @throws scala.UnsupportedOperationException if either temperature is in Celsius or Fahrenheit
   */
  override infix def remainder(that: Temperature): Double = {
    requireAbsolute("remainder", that)
    super.remainder(that)
  }

  /**
   * The whole quotient and remainder of dividing two temperatures, which must both be on an absolute scale
   * @throws scala.UnsupportedOperationException if either temperature is in Celsius or Fahrenheit
   */
  override infix def divideAndRemainder(that: Temperature): (Double, Temperature) = {
    requireAbsolute("divideAndRemainder", that)
    super.divideAndRemainder(that)
  }

  private def requireAbsolute(operation: String, that: Temperature): Unit =
    if (!unit.isAbsolute || !that.unit.isAbsolute) throw new UnsupportedOperationException(
      s"$operation is only supported between temperatures on an absolute scale (Kelvin or Rankine), not $this and $that - " +
        "use a.toKelvinScale / b.toKelvinScale for a ratio of temperatures, or a.toKelvinDegrees / b.toKelvinDegrees for a ratio of temperature differences")

  def *(that: ThermalCapacity): squants.energy.Energy = Joules(this.toKelvinScale * that.toJoulesPerKelvin)

  override def toString: String = unit match {
    case Kelvin => super.toString
    case _ => crossFormat(value) + unit.symbol // Non-Kelvin units are treated in a special manner, they do not get a space between the value and symbol.
  }

  def toString(unit: TemperatureScale): String = in(unit).toString

  private def convert(toScale: TemperatureScale, withOffset: Boolean = true): Temperature = (unit, toScale, withOffset) match {
    case (Fahrenheit, Fahrenheit, _) => this
    case (Celsius, Celsius, _) => this
    case (Kelvin, Kelvin, _) => this
    case (Rankine, Rankine, _) => this

    case (Fahrenheit, Celsius, true) => Celsius(TemperatureConversions.fahrenheitToCelsiusScale(value))
    case (Celsius, Fahrenheit, true) => Fahrenheit(TemperatureConversions.celsiusToFahrenheitScale(value))
    case (Celsius, Kelvin, true) => Kelvin(TemperatureConversions.celsiusToKelvinScale(value))
    case (Kelvin, Celsius, true) => Celsius(TemperatureConversions.kelvinToCelsiusScale(value))
    case (Fahrenheit, Kelvin, true) => Kelvin(TemperatureConversions.fahrenheitToKelvinScale(value))
    case (Kelvin, Fahrenheit, true) => Fahrenheit(TemperatureConversions.kelvinToFahrenheitScale(value))
    case (Fahrenheit, Rankine, true) => Rankine(TemperatureConversions.fahrenheitToRankineScale(value))
    case (Rankine, Fahrenheit, true) => Fahrenheit(TemperatureConversions.rankineToFahrenheitScale(value))
    case (Celsius, Rankine, true) => Rankine(TemperatureConversions.celsiusToRankineScale(value))
    case (Rankine, Celsius, true) => Celsius(TemperatureConversions.rankineToCelsiusScale(value))
    case (Kelvin, Rankine, true) => Rankine(TemperatureConversions.kelvinToRankineScale(value))
    case (Rankine, Kelvin, true) => Kelvin(TemperatureConversions.rankineToKelvinScale(value))

    case (Fahrenheit, Celsius, false) => Celsius(TemperatureConversions.fahrenheitToCelsiusDegrees(value))
    case (Celsius, Fahrenheit, false) => Fahrenheit(TemperatureConversions.celsiusToFahrenheitDegrees(value))
    case (Celsius, Kelvin, false) => Kelvin(TemperatureConversions.celsiusToKelvinDegrees(value))
    case (Kelvin, Celsius, false) => Celsius(TemperatureConversions.kelvinToCelsiusDegrees(value))
    case (Fahrenheit, Kelvin, false) => Kelvin(TemperatureConversions.fahrenheitToKelvinDegrees(value))
    case (Kelvin, Fahrenheit, false) => Fahrenheit(TemperatureConversions.kelvinToFahrenheitDegrees(value))
    case (Fahrenheit, Rankine, false) => Rankine(TemperatureConversions.fahrenheitToRankineDegrees(value))
    case (Rankine, Fahrenheit, false) => Fahrenheit(TemperatureConversions.rankineToFahrenheitDegrees(value))
    case (Celsius, Rankine, false) => Rankine(TemperatureConversions.celsiusToRankineDegrees(value))
    case (Rankine, Celsius, false) => Celsius(TemperatureConversions.rankineToCelsiusDegrees(value))
    case (Kelvin, Rankine, false) => Rankine(TemperatureConversions.kelvinToRankineDegrees(value))
    case (Rankine, Kelvin, false) => Kelvin(TemperatureConversions.rankineToKelvinDegrees(value))
  }

  infix def in(unit: TemperatureScale): Temperature = convert(unit, withOffset = true)
  def inFahrenheit: Temperature = convert(Fahrenheit)
  def inCelsius: Temperature = convert(Celsius)
  def inKelvin: Temperature = convert(Kelvin)

  infix def to(unit: TemperatureScale): Double = toScale(unit)
  def toScale(unit: TemperatureScale): Double = convert(unit, withOffset = true).value
  def toFahrenheitScale: Double = toScale(Fahrenheit)
  def toCelsiusScale: Double = toScale(Celsius)
  def toKelvinScale: Double = toScale(Kelvin)

  /** The irradiance a blackbody at this temperature radiates (Stefan-Boltzmann law): `E = sigma * T⁴`, with T in kelvin. NaN below absolute zero. */
  def blackbodyIrradiance: Irradiance = {
    val k = toKelvinScale
    WattsPerSquareMeter(if (k < 0) Double.NaN else PhysicalConstants.StefanBoltzmann * (k * k) * (k * k))
  }

  def toDegrees(unit: TemperatureScale): Double = convert(unit, withOffset = false).value
  def toFahrenheitDegrees: Double = toDegrees(Fahrenheit)
  def toCelsiusDegrees: Double = toDegrees(Celsius)
  def toKelvinDegrees: Double = toDegrees(Kelvin)
}

/**
 * Temperature companion object
 */
object Temperature extends Dimension[Temperature] with BaseDimension {
  def apply[A](n: A, scale: TemperatureScale)(using num: Numeric[A]) = new Temperature(num.toDouble(n), scale)

  private val TemperatureString = "([-+]?[0-9]*\\.?[0-9]+(?:[eE][-+]?[0-9]+)?) *°? *(f|F|c|C|k|K|r|R)".r

  def apply(s: String): Try[Temperature] = s match {
    case TemperatureString(value, scale) =>
      val unit = scale.nn match {
        case "f" | "F" => Fahrenheit
        case "c" | "C" => Celsius
        case "k" | "K" => Kelvin
        case _ => Rankine
      }
      Success(unit(value.nn.toDouble))
    case _ => Failure(QuantityParseException("Unable to parse Temperature", s))
  }

  def name = "Temperature"
  def primaryUnit = Kelvin
  def siUnit = Kelvin
  def units: Set[UnitOfMeasure[Temperature]] = Set(Kelvin, Fahrenheit, Celsius, Rankine)
  def dimensionSymbol = "Θ"
}

/**
 * Base trait for units of [[squants.thermal.Temperature]]
 */
sealed trait TemperatureScale extends UnitOfMeasure[Temperature] {
  def self: TemperatureScale

  /** True for a scale whose zero is absolute zero (Kelvin and Rankine) */
  def isAbsolute: Boolean = false
  def apply[A](n: A)(using num: Numeric[A]): Temperature = Temperature(num.toDouble(n), this)
}

object Celsius extends TemperatureScale {
  val symbol = "°C"
  val self = this
  protected def converterFrom: Double => Double = TemperatureConversions.celsiusToKelvinScale(_)
  protected def converterTo: Double => Double = TemperatureConversions.kelvinToCelsiusScale(_)
  def apply(temperature: Temperature): Temperature = temperature.inCelsius
}

object Fahrenheit extends TemperatureScale {
  val symbol = "°F"
  val self = this
  protected def converterFrom: Double => Double = TemperatureConversions.fahrenheitToKelvinScale(_)
  protected def converterTo: Double => Double = TemperatureConversions.kelvinToFahrenheitScale(_)
  def apply(temperature: Temperature): Temperature = temperature.inFahrenheit
}

object Kelvin extends TemperatureScale with PrimaryUnit with SiBaseUnit {
  val symbol = "K"
  val self = this
  override val isAbsolute = true
  def apply(temperature: Temperature): Temperature = temperature.inKelvin
}

object Rankine extends TemperatureScale {
  val symbol = "°R"
  val self = this
  override val isAbsolute = true
  protected def converterFrom: Double => Double = TemperatureConversions.rankineToKelvinScale(_)
  protected def converterTo: Double => Double = TemperatureConversions.kelvinToRankineScale(_)
  def apply(temperature: Temperature): Temperature = temperature.in(Rankine)
}

object TemperatureConversions {
  lazy val kelvin: Temperature = Kelvin(1)
  lazy val fahrenheit: Temperature = Fahrenheit(1)
  lazy val celsius: Temperature = Celsius(1)
  lazy val rankine: Temperature = Rankine(1)

  /*
   * Degree conversions are used to convert a quantity of degrees from one scale to another.
   * These conversions do not adjust for the zero offset.
   * Essentially they only do the 9:5 conversion between F degrees and C|K degrees
   */
  def celsiusToFahrenheitDegrees(celsius: Double): Double = celsius * 9d / 5d
  def fahrenheitToCelsiusDegrees(fahrenheit: Double): Double = fahrenheit * 5d / 9d
  def celsiusToKelvinDegrees(celsius: Double) = celsius
  def kelvinToCelsiusDegrees(kelvin: Double) = kelvin
  def fahrenheitToKelvinDegrees(fahrenheit: Double): Double = fahrenheit * 5d / 9d
  def kelvinToFahrenheitDegrees(kelvin: Double): Double = kelvin * 9d / 5d
  def celsiusToRankineDegrees(celsius: Double): Double = celsius * 9d / 5d
  def rankineToCelsiusDegrees(rankine: Double): Double = rankine * 5d / 9d
  def fahrenheitToRankineDegrees(fahrenheit: Double) = fahrenheit
  def rankineToFahrenheitDegrees(rankine: Double) = rankine
  def kelvinToRankineDegrees(kelvin: Double): Double = kelvin * 9d / 5d
  def rankineToKelvinDegrees(rankine: Double): Double = rankine * 5d / 9d

  /*
   * Scale conversions are used to convert a "thermometer" temperature from one scale to another.
   * These conversions will adjust the result by the zero offset.
   * They are used to find the equivalent absolute temperature in the other scale.
   */
  def celsiusToFahrenheitScale(celsius: Double): Double = celsius * 9d / 5d + 32d
  def fahrenheitToCelsiusScale(fahrenheit: Double): Double = (fahrenheit - 32d) * 5d / 9d
  def celsiusToKelvinScale(celsius: Double): Double = celsius + 273.15
  def kelvinToCelsiusScale(kelvin: Double): Double = kelvin - 273.15
  def fahrenheitToKelvinScale(fahrenheit: Double): Double = (fahrenheit + 459.67) * 5d / 9d
  def kelvinToFahrenheitScale(kelvin: Double): Double = kelvin * 9d / 5d - 459.67
  def celsiusToRankineScale(celsius: Double): Double = (celsius + 273.15) * 9d / 5d
  def rankineToCelsiusScale(rankine: Double): Double = (rankine - 491.67) * 5d / 9d
  def fahrenheitToRankineScale(fahrenheit: Double): Double = fahrenheit + 459.67
  def rankineToFahrenheitScale(rankine: Double): Double = rankine - 459.67
  def kelvinToRankineScale(kelvin: Double): Double = kelvin * 9d / 5d
  def rankineToKelvinScale(rankine: Double): Double = rankine * 5d / 9d

  extension [A](n: A)(using num: Numeric[A]) {
    def C: Temperature = Celsius(n)
    def celsius: Temperature = Celsius(n)
    def degreesCelsius: Temperature = Celsius(n)
    def F: Temperature = Fahrenheit(n)
    def Fah: Temperature = Fahrenheit(n) // F conflicts with (Float) in the console; Fah is provided as an alternative
    def fahrenheit: Temperature = Fahrenheit(n)
    def degreesFahrenheit: Temperature = Fahrenheit(n)
    def K: Temperature = Kelvin(n)
    def kelvin: Temperature = Kelvin(n)
    def degreesKelvin: Temperature = Kelvin(n)
    def R: Temperature = Rankine(n)
    def rankine: Temperature = Rankine(n)
    def degreesRankine: Temperature = Rankine(n)
  }

  extension (s: String) {
    def toTemperature: Try[Temperature] = Temperature(s)
  }
}

