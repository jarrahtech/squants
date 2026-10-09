package squants.bench

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations.*

import squants.space.{ Feet, Length, Meters, Yards }

/** Quantity.to and Quantity.in, within one unit and across units */
@State(Scope.Thread)
@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(2)
class ConversionBench {
  var feet: Length = Feet(12.5)
  var meters: Length = Meters(3.25)

  @Benchmark def toSameUnit: Double = feet.to(Feet)
  @Benchmark def toPrimaryUnit: Double = feet.to(Meters)
  @Benchmark def toFromPrimaryUnit: Double = meters.to(Feet)
  @Benchmark def toOtherUnit: Double = feet.to(Yards)
  @Benchmark def inSameUnit: Length = feet.in(Feet)
  @Benchmark def inOtherUnit: Length = feet.in(Yards)
}
