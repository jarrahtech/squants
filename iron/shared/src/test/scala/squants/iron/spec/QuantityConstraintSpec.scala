package squants.iron.spec

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import squants.iron.given
import squants.{Percent, Quantity}
import squants.information.Bits
import squants.mass.{Grams, GramsPerMillilitre, Kilograms, KilogramsPerCubicMeter, Mass, Pounds, Tonnes}
import squants.space.{InternationalMiles, Kilometers, Length, Meters}
import squants.thermal.{Celsius, Fahrenheit, Kelvin}
import squants.time.{Hours, Milliseconds, Seconds}

class QuantityConstraintSpec extends AnyFlatSpec with Matchers {

  behavior of "Iron constraints on quantities"

  private val fiveKilograms: Mass :| Positive = Kilograms(5).refineUnsafe[Positive]

  it should "refine a Mass with Positive" in {
    Kilograms(5).refineEither[Positive].isRight should be(true)
    Kilograms(0).refineEither[Positive].isLeft should be(true)
    Kilograms(-5).refineEither[Positive].isLeft should be(true)
  }

  it should "refine a Mass with Positive0" in {
    Kilograms(5).refineEither[Positive0].isRight should be(true)
    Kilograms(0).refineEither[Positive0].isRight should be(true)
    Kilograms(-5).refineEither[Positive0].isLeft should be(true)
  }

  it should "refine a Mass with Negative" in {
    Kilograms(-5).refineEither[Negative].isRight should be(true)
    Kilograms(0).refineEither[Negative].isLeft should be(true)
    Kilograms(5).refineEither[Negative].isLeft should be(true)
  }

  it should "refine a Length with Greater" in {
    Meters(6).refineEither[Greater[5]].isRight should be(true)
    Meters(5).refineEither[Greater[5]].isLeft should be(true)
    Meters(4).refineEither[Greater[5]].isLeft should be(true)
  }

  it should "refine a Length with Less" in {
    Meters(4).refineEither[Less[5]].isRight should be(true)
    Meters(5).refineEither[Less[5]].isLeft should be(true)
    Meters(6).refineEither[Less[5]].isLeft should be(true)
  }

  it should "refine a Mass with Interval.Closed" in {
    Kilograms(1).refineEither[Interval.Closed[1, 10]].isRight should be(true)
    Kilograms(10).refineEither[Interval.Closed[1, 10]].isRight should be(true)
    Kilograms(5.5).refineEither[Interval.Closed[1, 10]].isRight should be(true)
    Kilograms(0.99).refineEither[Interval.Closed[1, 10]].isLeft should be(true)
    Kilograms(10.01).refineEither[Interval.Closed[1, 10]].isLeft should be(true)
  }

  it should "refine a Time" in {
    Seconds(1).refineEither[Positive].isRight should be(true)
    Seconds(-1).refineEither[Positive].isLeft should be(true)
  }

  it should "refine a Temperature" in {
    Kelvin(300).refineEither[Positive].isRight should be(true)
    Kelvin(-1).refineEither[Positive].isLeft should be(true)
  }

  it should "refine a Density (a derived dimension)" in {
    KilogramsPerCubicMeter(1000).refineEither[Positive].isRight should be(true)
    KilogramsPerCubicMeter(-1).refineEither[Positive].isLeft should be(true)
    GramsPerMillilitre(1).refineEither[Greater[500]].isRight should be(true)
    GramsPerMillilitre(0.4).refineEither[Greater[500]].isLeft should be(true)
  }

  it should "test the value in the SI unit, whatever unit the quantity was built in" in {
    Kilometers(0.5).refineEither[Greater[499]].isRight should be(true)
    Meters(500).refineEither[Greater[499]].isRight should be(true)
    Kilometers(0.5).refineEither[Greater[501]].isLeft should be(true)
    Meters(500).refineEither[Greater[501]].isLeft should be(true)
    // 6 kg in three units, all more than 5 kg
    Kilograms(6).refineEither[Greater[5]].isRight should be(true)
    Grams(6000).refineEither[Greater[5]].isRight should be(true)
    Tonnes(0.006).refineEither[Greater[5]].isRight should be(true)
    // 2 tonnes is 2000 kg
    Tonnes(2).refineEither[Greater[1500]].isRight should be(true)
    Kilograms(2000).refineEither[Greater[1500]].isRight should be(true)
    // 1 mile is about 1609.34 m
    InternationalMiles(1).refineEither[Greater[1609]].isRight should be(true)
    InternationalMiles(1).refineEither[Greater[1610]].isLeft should be(true)
    // 10 pounds is about 4.536 kg
    Pounds(10).refineEither[Less[5]].isRight should be(true)
    Pounds(10).refineEither[Greater[5]].isLeft should be(true)
  }

  it should "test a Time in seconds, not in its primary unit (milliseconds)" in {
    Milliseconds(500).refineEither[Less[1]].isRight should be(true)
    Milliseconds(2000).refineEither[Less[1]].isLeft should be(true)
    Hours(1).refineEither[Greater[3599]].isRight should be(true)
    Hours(1).refineEither[Less[3601]].isRight should be(true)
  }

  it should "test a Temperature on the Kelvin scale, including offset scales" in {
    Celsius(-10).refineEither[Positive].isRight should be(true) // 263.15 K
    Kelvin(0).refineEither[Positive].isLeft should be(true)
    Celsius(-300).refineEither[Positive].isLeft should be(true) // -26.85 K
    Celsius(0).refineEither[Greater[273]].isRight should be(true) // 273.15 K
    Celsius(0).refineEither[Less[273]].isLeft should be(true)
    Fahrenheit(32).refineEither[Greater[273]].isRight should be(true) // 273.15 K
    Fahrenheit(-459.67).refineEither[Positive0].isRight should be(true) // 0 K
  }

  it should "use the SI unit each dimension declares, even when that is not the obvious one" in {
    // Information's SI unit is bytes: 4 bits is half a byte.
    Bits(4).refineEither[Less[1]].isRight should be(true)
    // Dimensionless's SI unit is Each: 50 percent is 0.5.
    Percent(50).refineEither[Less[1]].isRight should be(true)
    Percent(150).refineEither[Less[1]].isLeft should be(true)
  }

  it should "treat a value exactly on a boundary in another unit as on the boundary" in {
    Grams(5000).refineEither[Greater[5]].isLeft should be(true)
    Grams(5000).refineEither[Less[5]].isLeft should be(true)
    Tonnes(0.005).refineEither[Greater[5]].isLeft should be(true)
    Kilometers(0.5).refineEither[Greater[500]].isLeft should be(true)
    Hours(1).refineEither[Greater[3600]].isLeft should be(true)
    Celsius(0).refineEither[Greater[273.15]].isLeft should be(true)
    Celsius(-273.15).refineEither[Positive0].isRight should be(true)
    Fahrenheit(-459.67).refineEither[Positive].isLeft should be(true)
  }

  it should "report the same failure message as Iron does for a plain Double" in {
    val plain = 1.0.refineEither[Greater[5]].left.toOption.get
    Kilograms(1).refineEither[Greater[5]].left.toOption.get should be(plain)
    Kilograms(-1).refineEither[Positive].left.toOption.get should be((-1.0).refineEither[Positive].left.toOption.get)
  }

  it should "support refineUnsafe" in {
    Kilograms(5).refineUnsafe[Positive] should be(Kilograms(5))
    an[IllegalArgumentException] should be thrownBy Kilograms(-5).refineUnsafe[Positive]
  }

  it should "keep the value and unit of the quantity it refines" in {
    val m = Kilometers(0.5).refineUnsafe[Positive]
    m.value should be(0.5)
    m.unit should be(Kilometers)
  }

  it should "accept a refined quantity wherever the plain quantity is expected" in {
    def takesMass(x: Mass): Mass = x
    takesMass(fiveKilograms) should be(Kilograms(5))
    val l: Length :| Greater[0] = Meters(3).refineUnsafe[Greater[0]]
    (l + Meters(1)) should be(Meters(4))
  }

  it should "give back the plain type from arithmetic on a refined quantity" in {
    val diff: Mass = fiveKilograms - Kilograms(10)
    diff should be(Kilograms(-5))
    assertDoesNotCompile("val r: Mass :| Positive = fiveKilograms - Kilograms(10)")
  }

  it should "reject Money at compile time, because Money has no SI unit" in {
    assertDoesNotCompile("squants.market.USD(5).refineEither[Positive]")
  }

  it should "fail with a plain exception, not NotImplementedError, if Money reaches the given through generic code" in {
    // In code generic over Q the compiler cannot tell it is Money, so the compile-time exclusion does not apply.
    def check[Q <: Quantity[Q]](q: Q) = q.refineEither[Positive]
    an[IllegalArgumentException] should be thrownBy check(squants.market.USD(5))
  }

  it should "not treat a quantity constructor as a refined literal" in {
    assertDoesNotCompile("val m: Mass :| Positive = Kilograms(5)")
  }
}
