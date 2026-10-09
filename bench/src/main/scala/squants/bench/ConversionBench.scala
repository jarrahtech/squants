package squants.bench

import org.openjdk.jmh.annotations.Benchmark

import squants.space.{ Feet, Length, Meters, Yards }

/** Quantity.to and Quantity.in, within one unit and across units */
class ConversionBench extends SquantsBench {
  var feet: Length = Feet(12.5)
  var meters: Length = Meters(3.25)

  @Benchmark def toSameUnit: Double = feet.to(Feet)
  @Benchmark def toPrimaryUnit: Double = feet.to(Meters)
  @Benchmark def toFromPrimaryUnit: Double = meters.to(Feet)
  @Benchmark def toOtherUnit: Double = feet.to(Yards)
  @Benchmark def inSameUnit: Length = feet.in(Feet)
  @Benchmark def inOtherUnit: Length = feet.in(Yards)
}
