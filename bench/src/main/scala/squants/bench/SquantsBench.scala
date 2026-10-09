package squants.bench

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations.*

/** The JMH settings every benchmark shares, so that results from different classes are comparable */
@State(Scope.Thread)
@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(2)
abstract class SquantsBench
