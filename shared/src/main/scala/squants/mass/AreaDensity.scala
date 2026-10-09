/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2013-2015, Gary Keorkunian                                       **
**                                                                      **
\*                                                                      */

package squants.mass

import squants._
import squants.space.Acres
import scala.util.Try

/**
 * @author  garyKeorkunian
 * @since   0.2.3
 *
 * @param value Double
 */
final class AreaDensity private (val value: Double, val unit: AreaDensityUnit)
  extends Quantity[AreaDensity] {

  def dimension = AreaDensity

  def *(that: Area): Mass = Kilograms(this.toKilogramsPerSquareMeter * that.toSquareMeters)

  def toKilogramsPerSquareMeter: Double = to(KilogramsPerSquareMeter)
  def toGramsPerSquareCentimeter: Double = to(GramsPerSquareCentimeter)
  def toKilogramsPerHectare: Double = to(KilogramsPerHectare)
  def toPoundsPerAcre: Double = to(PoundsPerAcre)
}

/**
 * Factory singleton for [[squants.mass.AreaDensity]] values
 */
object AreaDensity extends Dimension[AreaDensity] {
  private[mass] def apply[A](n: A, unit: AreaDensityUnit)(using num: Numeric[A]) = new AreaDensity(num.toDouble(n), unit)
  def apply(mass: Mass, area: Area): AreaDensity = KilogramsPerSquareMeter(mass.toKilograms / area.toSquareMeters)
  def apply(value: Any): Try[AreaDensity] = parse(value)
  def name = "AreaDensity"
  def primaryUnit = KilogramsPerSquareMeter
  def siUnit = KilogramsPerSquareMeter
  def units: Set[UnitOfMeasure[AreaDensity]] = Set(KilogramsPerSquareMeter, KilogramsPerHectare, GramsPerSquareCentimeter, PoundsPerAcre)
}

trait AreaDensityUnit extends UnitOfMeasure[AreaDensity] {
  def apply[A](n: A)(using num: Numeric[A]): AreaDensity = AreaDensity(n, this)
}

object KilogramsPerSquareMeter extends AreaDensityUnit with PrimaryUnit with SiUnit {
  val symbol = "kg/m²"
}

object KilogramsPerHectare extends AreaDensityUnit with UnitConverter {
  val symbol = "kg/hectare"
  val conversionFactor: Double = 1 / (100 * 100d)
}

object GramsPerSquareCentimeter extends AreaDensityUnit with UnitConverter with SiUnit {
  val symbol = "g/cm²"
  val conversionFactor: Double = (100 * 100d) / 1000d
}

object PoundsPerAcre extends AreaDensityUnit with UnitConverter {
  val symbol: String = s"${Pounds.symbol}/${Acres.symbol}"
  // Base unit is kg/m^2
  import squants.mass.MassConversions.{ pound, kilogram }
  import squants.space.AreaConversions.{ acre, squareMeter }
  val conversionFactor: Double = (pound / kilogram) / (acre / squareMeter)
}

object AreaDensityConversions {
  lazy val kilogramPerSquareMeter: AreaDensity = KilogramsPerSquareMeter(1)
  lazy val kilogramPerHectare: AreaDensity = KilogramsPerHectare(1)
  lazy val gramPerSquareCentimeter: AreaDensity = GramsPerSquareCentimeter(1)
  lazy val poundsPerAcre: AreaDensity = PoundsPerAcre(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def kilogramsPerSquareMeter: AreaDensity = KilogramsPerSquareMeter(n)
    def kilogramsPerHectare: AreaDensity = KilogramsPerHectare(n)
    def gramsPerSquareCentimeter: AreaDensity = GramsPerSquareCentimeter(n)
  }

  given AreaDensityNumeric: AbstractQuantityNumeric[AreaDensity](AreaDensity.primaryUnit) {}
}
