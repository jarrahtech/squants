package squants.iron

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.*
import squants.{Quantity, UnitConverter, UnitOfMeasure}

/**
 * Marks an Iron constraint on a number that still holds after multiplying by any positive factor, which is what
 * converting between plain units does: the sign constraints (`Positive`, `Positive0`, `Negative`, `Negative0` and
 * `Greater[0]`, `GreaterEqual[0]`, `Less[0]`, `LessEqual[0]`).
 *
 * A constraint such as `Greater[5]` is not one: 10 is more than 5, but 10 g is not more than 5 kg.
 *
 * Add an instance for your own constraint only if it really is preserved by multiplying by a positive factor:
 * {{{
 * given SignPreserving[MyPositive] = SignPreserving.instance
 * }}}
 */
trait SignPreserving[C]

object SignPreserving {
  def instance[C]: SignPreserving[C] = new SignPreserving[C] {}

  given SignPreserving[Positive] = instance
  given SignPreserving[Positive0] = instance
  given SignPreserving[Negative] = instance
  given SignPreserving[Negative0] = instance
  given SignPreserving[Greater[0]] = instance
  given SignPreserving[GreaterEqual[0]] = instance
  given SignPreserving[Less[0]] = instance
  given SignPreserving[LessEqual[0]] = instance
}

/**
 * Builds a refined quantity from a number Iron has checked at compile time, so a bad constant is a compile error
 * instead of a runtime failure.
 *
 * {{{
 * import io.github.iltotore.iron.*
 * import io.github.iltotore.iron.autoRefine   // checks literals at compile time
 * import squants.iron.*
 *
 * val m: Mass :| Positive = Kilograms.refined[Positive](5.0)   // compiles
 * Kilograms.refined[Positive](-5.0)                            // does not compile
 *
 * val d: Double :| Positive = 5.0                              // a number checked once...
 * Kilograms.refined(d)                                         // ...and reused; the constraint is inferred
 * }}}
 *
 * The compile-time check is Iron's `autoRefine` conversion, so it needs that import. (Iron's `.refine[C]` is not a
 * compile-time check: in Iron 3 it is a deprecated alias of the runtime `refineUnsafe`.)
 *
 * Only for constraints that survive a change of unit ([[SignPreserving]]), and only for units that are a plain
 * multiple of the SI unit ([[squants.UnitConverter]]). That leaves out `Celsius` and `Fahrenheit`, which have an offset,
 * so even the sign changes (-10 degrees Celsius is above zero kelvin); `Rankine`, which has no offset but is not
 * defined as a plain-factor unit in Squants; and `Money`. `Kelvin` is fine. A number that is only known at runtime is
 * not a literal: refine the quantity at runtime with `refineEither`, `refineOption` or `refineUnsafe`.
 */
extension [Q <: Quantity[Q]](unit: UnitOfMeasure[Q] & UnitConverter)
  def refined[C](value: Double :| C)(using SignPreserving[C]): Q :| C = unit(value: Double).assume[C]
