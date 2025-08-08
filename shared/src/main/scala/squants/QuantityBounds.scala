package squants

// Similar to squants.QuantityRange but can be a point (ie lower==upper is allowed)
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

  def shiftLower(that: A) = QuantityBounds(this.lower + that, this.upper)
  def +=(that: A) = shiftLower(that)
  def -=(that: A) = shiftLower(-that)

  def expand(that: A) = QuantityBounds(this.lower - that, this.upper + that)
  def -+(that: A) = expand(that)

  def shrink(that: A) = QuantityBounds(this.lower + that, this.upper - that)
  def +-(that: A) = shrink(that)

  def contains(q: A) = q > lower && q < upper
  def contains(that: QuantityBounds[A]): Boolean = contains(that.lower) && contains(that.upper)
  
  def includes(q: A) = q >= lower && q <= upper
  def includes(that: QuantityBounds[A]): Boolean = includes(that.lower) && includes(that.upper)

  lazy val toQuantity = upper - lower
  lazy val toSeq: Seq[A] = Seq(lower, upper)
  lazy val toList: List[A] = List(lower, upper)
  lazy val toTuple: (lower: A, upper: A) = (lower, upper)

  def lerp(ratio: Double) = lower + toQuantity*ratio
  lazy val mid = lerp(0.5d)
  def ratio(value: Quantity[A]) = (value - lower)/toQuantity
  def clamp(value: Quantity[A]) = value.max(lower).min(upper)
}
