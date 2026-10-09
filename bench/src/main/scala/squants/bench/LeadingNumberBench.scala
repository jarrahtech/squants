package squants.bench

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations.*

import squants.*
import squants.energy.Power
import squants.energy.PowerConversions.*
import squants.space.Meters
import squants.time.Frequency

/** The DSL forms where a number comes first */
@State(Scope.Thread)
@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(2)
class LeadingNumberBench {
  var meters: Length = Meters(3.25)
  var seconds: Time = Seconds(2)
  var double: Double = 2.5
  var int: Int = 3
  var long: Long = 4L
  var bigDecimal: BigDecimal = BigDecimal(5)

  @Benchmark def quantityTimesDouble: Length = meters * double
  @Benchmark def doubleTimesQuantity: Length = double * meters
  @Benchmark def intTimesQuantity: Length = int * meters
  @Benchmark def longTimesQuantity: Length = long * meters
  @Benchmark def bigDecimalTimesQuantity: Length = bigDecimal * meters
  @Benchmark def doublePerTime: Frequency = double / seconds
  @Benchmark def doubleUnitSuffix: Power = double.kW
  @Benchmark def intUnitSuffix: Power = int.kW
}
