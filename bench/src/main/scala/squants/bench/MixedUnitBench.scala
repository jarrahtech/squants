package squants.bench

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations.*

import squants.space.{ Length, Meters }

/**
 * Conversions over quantities in every Length unit. A loop over one unit lets the JVM specialise the conversion
 * for it; this is the case where it cannot. Times are for one pass over the whole array.
 */
@State(Scope.Thread)
@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(2)
class MixedUnitBench {
  var lengths: Array[Length] = Length.units.toArray.sortBy(_.symbol).map(_(12.5))
  var meters: Length = Meters(3.25)

  @Benchmark def toPrimaryUnit: Double = {
    var sum = 0d
    var i = 0
    while (i < lengths.length) { sum += lengths(i).to(Meters); i += 1 }
    sum
  }

  @Benchmark def plusPrimaryUnit: Double = {
    var sum = 0d
    var i = 0
    while (i < lengths.length) { sum += (lengths(i) + meters).value; i += 1 }
    sum
  }

  @Benchmark def comparePrimaryUnit: Int = {
    var sum = 0
    var i = 0
    while (i < lengths.length) { sum += lengths(i).compare(meters); i += 1 }
    sum
  }

  @Benchmark def sort: Array[Length] = lengths.sorted
}
