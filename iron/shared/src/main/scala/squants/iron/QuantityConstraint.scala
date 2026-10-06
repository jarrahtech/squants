package squants.iron

import io.github.iltotore.iron.RuntimeConstraint
import squants.Quantity
import squants.market.Money

import scala.util.NotGiven

/**
 * Lets any Squants quantity be refined with an Iron constraint, for example `Mass :| Positive`.
 *
 * The constraint is derived from Iron's own constraint for `Double`, and the value is tested in the SI unit of the
 * quantity's dimension (`dimension.siUnit`), so `Mass :| Greater[5]` means "more than 5 kg" whatever unit the
 * quantity was created in. (Not the primary unit: `Time`'s primary unit is milliseconds, its SI unit is seconds.)
 * Offset scales are converted properly, so `Celsius(-10)` is 263.15 K and passes `Positive`.
 *
 * Import it where you refine quantities:
 * {{{
 * import io.github.iltotore.iron.*
 * import io.github.iltotore.iron.constraint.numeric.*
 * import squants.iron.given
 *
 * val m: Either[String, Mass :| Positive] = Kilograms(5).refineEither[Positive]
 * }}}
 *
 * This is a [[RuntimeConstraint]], which is what `refineEither`, `refineOption` and `refineUnsafe` use, so the check
 * happens when the program runs. `Kilograms(5)` is a method call, not a literal, so Iron cannot check it while
 * compiling; for constants that should be checked at compile time see [[refined]].
 *
 * `Money` is excluded: it is `BigDecimal`-backed and has no SI unit (`Money.siUnit` is `???`). Where the compiler knows
 * the type, `USD(5).refineEither[Positive]` fails to compile. In code generic over `Q <: Quantity[Q]` it cannot know,
 * so a `Money` that gets here fails with an `IllegalArgumentException` instead of the `NotImplementedError` that
 * `Money.siUnit` would throw.
 */
given quantityRuntimeConstraint[Q <: Quantity[Q], C](using constraint: RuntimeConstraint[Double, C])(using NotGiven[Q <:< Money]): RuntimeConstraint[Q, C] =
  RuntimeConstraint(q => constraint.test(inSiUnit(q)), constraint.message)

private def inSiUnit[Q <: Quantity[Q]](q: Q): Double = q match {
  case _: Money => throw new IllegalArgumentException("Money has no SI unit, so it cannot be refined")
  case _ => q.to(q.dimension.siUnit)
}
