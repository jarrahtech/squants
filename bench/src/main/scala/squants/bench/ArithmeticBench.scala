package squants.bench

import org.openjdk.jmh.annotations.Benchmark

import squants.space.{ Feet, Length, Meters }

/** The operators every Quantity has, with the right operand in the same unit and in a different one */
class ArithmeticBench extends SquantsBench {
  var feet: Length = Feet(12.5)
  var moreFeet: Length = Feet(3.75)
  var meters: Length = Meters(3.25)
  var scalar: Double = 1.5

  @Benchmark def plusSameUnit: Length = feet + moreFeet
  @Benchmark def plusOtherUnit: Length = feet + meters
  @Benchmark def timesDouble: Length = feet * scalar
  @Benchmark def divideSameUnit: Double = feet / moreFeet
  @Benchmark def divideOtherUnit: Double = feet / meters
  @Benchmark def compareSameUnit: Int = feet.compare(moreFeet)
  @Benchmark def compareOtherUnit: Int = feet.compare(meters)
  @Benchmark def equalsSameUnit: Boolean = feet == moreFeet
  @Benchmark def equalsOtherUnit: Boolean = feet == meters
  @Benchmark def hash: Int = feet.hashCode
}
