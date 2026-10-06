package squants.iron

import io.github.iltotore.iron.RuntimeConstraint
import squants.Quantity

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
 * This is a [[RuntimeConstraint]], which is what `refineEither`, `refineOption` and `refineUnsafe` use. It does not
 * support Iron's compile-time `refine`, because `Kilograms(5)` is a method call and not a literal.
 */
given quantityRuntimeConstraint[Q <: Quantity[Q], C](using constraint: RuntimeConstraint[Double, C]): RuntimeConstraint[Q, C] =
  RuntimeConstraint(q => constraint.test(q.to(q.dimension.siUnit)), constraint.message)
