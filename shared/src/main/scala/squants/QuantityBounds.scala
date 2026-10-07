package squants

/**
 * A lower and an upper bound on a quantity. Like [[squants.QuantityRange]] but the bounds may be equal, which makes it a
 * point (`isPoint`); a `QuantityRange` needs `lower < upper`.
 *
 * Membership has two methods that differ only at the ends:
 *  - `contains` is strict: both ends are excluded, so a point contains nothing, not even itself.
 *  - `includes` is inclusive: both ends count, so a point includes itself.
 */
final case class QuantityBounds[A <: Quantity[A]](lower: A, upper: A) {
  require(lower<=upper, "Lower bound must be equal or smaller than upper")

  def isPoint = lower==upper
  def map[B <: Quantity[B]](op: A => B): QuantityBounds[B] = QuantityBounds(op(lower), op(upper))

  def shift(that: A) = QuantityBounds(this.lower + that, this.upper + that)
  def ++(that: A) = shift(that)
  def --(that: A) = shift(-that)

  def shiftUpper(that: A) = QuantityBounds(this.lower, this.upper + that)
  def =+(that: A) = shiftUpper(that)
  def =-(that: A) = shiftUpper(-that)

  // No operator alias: `+=` and `-=` would read as mutation.
  def shiftLower(that: A) = QuantityBounds(this.lower + that, this.upper)

  def expand(that: A) = QuantityBounds(this.lower - that, this.upper + that)
  def -+(that: A) = expand(that)

  // No operator alias: `+-` already means "plus or minus" on `Quantity`.
  def shrink(that: A) = QuantityBounds(this.lower + that, this.upper - that)

  /** True if `q` is strictly between the bounds: both ends are excluded. */
  def contains(q: A) = q > lower && q < upper
  def contains(that: QuantityBounds[A]): Boolean = contains(that.lower) && contains(that.upper)

  /** True if `q` is between the bounds or equal to one of them: both ends count. */
  def includes(q: A) = q >= lower && q <= upper
  def includes(that: QuantityBounds[A]): Boolean = includes(that.lower) && includes(that.upper)

  /** The same bounds as a [[QuantityRange]], or None for a point, which a range (lower < upper) cannot represent. */
  def toRange: Option[QuantityRange[A]] = if (isPoint) None else Some(QuantityRange(lower, upper))

  lazy val toQuantity = upper - lower
  def toSeq: Seq[A] = Seq(lower, upper)
  def toList: List[A] = List(lower, upper)
  def toTuple: (lower: A, upper: A) = (lower, upper)

  /** The value `ratio` of the way from `lower` to `upper`; below `lower` or above `upper` it extrapolates. */
  def lerp(ratio: Double) = lower + toQuantity*ratio
  def mid = lerp(0.5d)

  /**
   * How far `value` is from `lower`, as a fraction of the length: 0 at `lower`, 1 at `upper`, outside [0, 1] when
   * `value` is outside the bounds. The inverse of `lerp`. For a point the length is zero, so the result is NaN or infinite.
   */
  def ratio(value: A): Double = (value - lower)/toQuantity

  def clamp(value: A): A = value.max(lower).min(upper)
}

object QuantityBounds {
  def fromRange[A <: Quantity[A]](range: QuantityRange[A]): QuantityBounds[A] = QuantityBounds(range.lower, range.upper)
}
