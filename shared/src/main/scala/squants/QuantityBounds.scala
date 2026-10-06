package squants

/**
 * A lower and an upper bound on a quantity. Like [[squants.QuantityRange]] but the bounds may be equal, which makes it a
 * point (`isPoint`); a `QuantityRange` needs `lower < upper`.
 *
 * Membership has two methods that differ only at the ends:
 *  - `contains` is strict: both ends are excluded, so a point contains nothing, not even itself.
 *  - `includes` is inclusive: both ends count, so a point includes itself.
 *
 * Every operation returns a new `QuantityBounds`; none mutates.
 */
final case class QuantityBounds[A <: Quantity[A]](lower: A, upper: A) {
  require(lower<=upper, "Lower bound must be equal or smaller than upper")

  def isPoint = lower==upper
  def map[B <: Quantity[B]](op: A => B): QuantityBounds[B] = QuantityBounds(op(lower), op(upper))

  /** Moves both bounds by `that`. */
  def shift(that: A) = QuantityBounds(this.lower + that, this.upper + that)
  def ++(that: A) = shift(that)
  def --(that: A) = shift(-that)

  /** Moves only the upper bound by `that`. */
  def shiftUpper(that: A) = QuantityBounds(this.lower, this.upper + that)
  def =+(that: A) = shiftUpper(that)
  def =-(that: A) = shiftUpper(-that)

  /** Moves only the lower bound by `that`. (No operator alias: `+=` and `-=` would read as mutation.) */
  def shiftLower(that: A) = QuantityBounds(this.lower + that, this.upper)

  /** Moves the lower bound down and the upper bound up by `that`. */
  def expand(that: A) = QuantityBounds(this.lower - that, this.upper + that)
  def -+(that: A) = expand(that)

  /** Moves the lower bound up and the upper bound down by `that`. (No operator alias: `+-` already means "plus or minus" on `Quantity`.) */
  def shrink(that: A) = QuantityBounds(this.lower + that, this.upper - that)

  /** True if `q` is strictly between the bounds. Both ends are excluded; see [[includes]] for the inclusive test. */
  def contains(q: A) = q > lower && q < upper
  /** True if `that` is strictly inside these bounds: both of its ends are strictly between this one's ends. */
  def contains(that: QuantityBounds[A]): Boolean = contains(that.lower) && contains(that.upper)

  /** True if `q` is between the bounds or equal to one of them. Both ends count; see [[contains]] for the strict test. */
  def includes(q: A) = q >= lower && q <= upper
  /** True if `that` fits inside these bounds, sharing an end with them if it likes. */
  def includes(that: QuantityBounds[A]): Boolean = includes(that.lower) && includes(that.upper)

  /** The same bounds as a [[QuantityRange]], or None for a point, which a range (lower < upper) cannot represent. */
  def toRange: Option[QuantityRange[A]] = if (isPoint) None else Some(QuantityRange(lower, upper))

  lazy val toQuantity = upper - lower
  lazy val toSeq: Seq[A] = Seq(lower, upper)
  lazy val toList: List[A] = List(lower, upper)
  lazy val toTuple: (lower: A, upper: A) = (lower, upper)

  /** The value `ratio` of the way from `lower` to `upper`; below `lower` or above `upper` it extrapolates. */
  def lerp(ratio: Double) = lower + toQuantity*ratio
  lazy val mid = lerp(0.5d)

  /**
   * How far `value` is from `lower`, as a fraction of the length: 0 at `lower`, 1 at `upper`, outside [0, 1] when
   * `value` is outside the bounds. The inverse of `lerp`. For a point the length is zero, so the result is NaN or infinite.
   */
  def ratio(value: A): Double = (value - lower)/toQuantity

  /** `value` limited to the bounds: `lower` if it is below, `upper` if it is above, otherwise `value` itself. */
  def clamp(value: A): A = value.max(lower).min(upper)
}

object QuantityBounds {
  /** The bounds of a [[QuantityRange]]. */
  def fromRange[A <: Quantity[A]](range: QuantityRange[A]): QuantityBounds[A] = QuantityBounds(range.lower, range.upper)
}
