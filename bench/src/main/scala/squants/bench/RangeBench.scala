package squants.bench

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations.*

import squants.{ QuantityRange, QuantitySeries }
import squants.space.{ Length, Meters }

/** QuantityRange, which builds one range per step */
@State(Scope.Thread)
@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(2)
class RangeBench {
  var range: QuantityRange[Length] = QuantityRange(Meters(0), Meters(100))
  var step: Length = Meters(1)
  var point: Length = Meters(42)

  @Benchmark def create: QuantityRange[Length] = QuantityRange(step, point)
  @Benchmark def size: Length = range.toQuantity
  @Benchmark def contains: Boolean = range.contains(point)
  @Benchmark def times10: QuantitySeries[Length] = range * 10
  @Benchmark def divideInto100: QuantitySeries[Length] = range / step
  @Benchmark def increment: QuantityRange[Length] = range.inc
}
