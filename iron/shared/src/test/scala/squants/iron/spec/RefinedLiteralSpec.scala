package squants.iron.spec

import io.github.iltotore.iron.*
import io.github.iltotore.iron.autoRefine
import io.github.iltotore.iron.constraint.numeric.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import squants.iron.*
import squants.iron.given
import squants.mass.{Grams, Kilograms, KilogramsPerCubicMeter, Mass}
import squants.space.Meters
import squants.thermal.Kelvin
import squants.time.Seconds

class RefinedLiteralSpec extends AnyFlatSpec with Matchers {

  behavior of "refined, a constructor for numbers Iron has checked at compile time"

  def weigh(m: Mass :| Positive): Double = m.toKilograms

  it should "build a refined quantity from a literal, in the unit it was called on" in {
    val m: Mass :| Positive = Kilograms.refined[Positive](5.0)
    m should be(Kilograms(5))
    m.unit should be(Kilograms)
    m.value should be(5.0)
    weigh(Grams.refined[Positive](250.0)) should be(0.25)
  }

  it should "infer the constraint from a Double that was checked earlier" in {
    val d: Double :| Positive = 5.0
    val m = Kilograms.refined(d)
    weigh(m) should be(5.0)
  }

  it should "work for every sign constraint, named or written directly" in {
    Kilograms.refined[Positive](1.0) should be(Kilograms(1))
    Kilograms.refined[Positive0](0.0) should be(Kilograms(0))
    Kilograms.refined[Negative](-1.0) should be(Kilograms(-1))
    Kilograms.refined[Negative0](0.0) should be(Kilograms(0))
    Kilograms.refined[Greater[0]](1.0) should be(Kilograms(1))
    Kilograms.refined[GreaterEqual[0]](0.0) should be(Kilograms(0))
    Kilograms.refined[Less[0]](-1.0) should be(Kilograms(-1))
    Kilograms.refined[LessEqual[0]](0.0) should be(Kilograms(0))
  }

  it should "work for other dimensions and for Kelvin" in {
    Meters.refined[Positive](2.0) should be(Meters(2))
    Seconds.refined[Positive](2.0) should be(Seconds(2))
    KilogramsPerCubicMeter.refined[Positive](1000.0) should be(KilogramsPerCubicMeter(1000))
    Kelvin.refined[Positive](300.0) should be(Kelvin(300))
  }

  it should "give a quantity that also passes the runtime refinement" in {
    val positive: Mass = Grams.refined[Positive](5.0) // widened: an already refined value is not refined again
    val negative: Mass = Grams.refined[Negative](-5.0)
    positive.refineEither[Positive].isRight should be(true)
    negative.refineEither[Negative].isRight should be(true)
  }

  it should "reject a literal that breaks the constraint, at compile time" in {
    assertDoesNotCompile("Kilograms.refined[Positive](-5.0)")
    assertDoesNotCompile("Kilograms.refined[Positive](0.0)")
    assertDoesNotCompile("Kilograms.refined[Positive0](-1.0)")
    assertDoesNotCompile("Kilograms.refined[Negative](1.0)")
    assertDoesNotCompile("Kilograms.refined[Negative0](1.0)")
  }

  it should "reject a constraint that does not survive a change of unit" in {
    // 10 g is not more than 5 kg, so the number alone says nothing about the SI value.
    assertDoesNotCompile("Kilograms.refined[Greater[5]](10.0)")
    assertDoesNotCompile("Grams.refined[Interval.Closed[1, 10]](5.0)")
  }

  it should "reject units that are not a plain multiple of the SI unit" in {
    // -10 degrees Celsius is above zero kelvin, so even the sign changes.
    assertDoesNotCompile("squants.thermal.Celsius.refined[Positive](5.0)")
    assertDoesNotCompile("squants.thermal.Fahrenheit.refined[Positive](5.0)")
    assertDoesNotCompile("squants.thermal.Rankine.refined[Positive](5.0)") // not defined as a plain factor either
    assertDoesNotCompile("squants.market.USD.refined[Positive](5.0)")
  }

  it should "reject a number that is only known at runtime" in {
    val runtime: Double = scala.util.Random.nextDouble() + 1
    assertDoesNotCompile("Kilograms.refined[Positive](runtime)")
    // ... which is what refineEither is for.
    Kilograms(runtime).refineEither[Positive].isRight should be(true)
  }

  it should "let a user add a constraint that also preserves the sign" in {
    given SignPreserving[Less[-5]] = SignPreserving.instance
    Kilograms.refined[Less[-5]](-10.0) should be(Kilograms(-10))
  }
}
