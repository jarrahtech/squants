/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2015-2017, Carlos Quiroz                                         **
**                                                                      **
\*                                                                      */

package squants.electro

import squants._
import squants.motion.Newtons
import scala.util.Try

/**
 * Quantity and units for Permeability
 *
 * https://en.wikipedia.org/wiki/Permeability_(electromagnetism)
 *
 * @author  cquiroz
 * @since   1.4
 *
 * @param value value in [[squants.electro.HenriesPerMeter]]
 */
final class Permeability private (val value: Double, val unit: PermeabilityUnit)
  extends Quantity[Permeability] {

  val dimension = Permeability

  def *(that: Length): Inductance = Henry(this.toHenriesPerMeter * that.toMeters)

  def toHenriesPerMeter: Double = to(HenriesPerMeter)
  def toNewtonsPerAmpereSquared: Double = to(NewtonsPerAmperesSquared)
}

object Permeability extends Dimension[Permeability] {
  private[electro] def apply[A](n: A, unit: PermeabilityUnit)(using num: Numeric[A]) = new Permeability(num.toDouble(n), unit)
  def apply(value: Any): Try[Permeability] = parse(value)
  val name = "Permeability"
  val primaryUnit = HenriesPerMeter
  val siUnit = HenriesPerMeter
  val units: Set[UnitOfMeasure[Permeability]] = Set[UnitOfMeasure[Permeability]](HenriesPerMeter, NewtonsPerAmperesSquared)
}

trait PermeabilityUnit extends UnitOfMeasure[Permeability] with UnitConverter {
  def apply[A](n: A)(using num: Numeric[A]): Permeability = Permeability(n, this)
}

object HenriesPerMeter extends PermeabilityUnit with PrimaryUnit with SiUnit {
  val symbol: String = s"${Henry.symbol}/${Meters.symbol}"
}

object NewtonsPerAmperesSquared extends PermeabilityUnit with PrimaryUnit with SiUnit {
  val symbol: String = s"${Newtons.symbol}/${Amperes.symbol}²"
}

object PermeabilityConversions {
  lazy val henriesPerMeter: Permeability = HenriesPerMeter(1)
  lazy val newtonsPerAmperesSquared: Permeability = NewtonsPerAmperesSquared(1)

  extension [A](n: A)(using num: Numeric[A]) {
    def henriesPerMeter: Permeability = HenriesPerMeter(n)
    def newtonsPerAmperesSquared: Permeability = NewtonsPerAmperesSquared(n)
  }

  given PermeabilityNumeric: AbstractQuantityNumeric[Permeability](Permeability.primaryUnit) {}
}
