package squants.iron.spec

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.*
import org.scalacheck.Prop.forAll
import org.scalacheck.{Gen, Properties}
import squants.iron.given
import squants.mass.Mass
import squants.thermal.{Celsius, Fahrenheit, Kelvin, Rankine, Temperature}
import squants.time.{Days, EarthGigaYears, EarthMegaYears, EarthYears, Hours, Microseconds, Milliseconds, Minutes, Nanoseconds, Seconds, Time}

/**
 * Each property compares refinement with an oracle written independently of Squants' conversion code, so a
 * conversion bug cannot hide by appearing on both sides.
 */
object QuantityConstraintChecks extends Properties("QuantityConstraint") {

  private val values: Gen[Double] = Gen.choose(-1e6, 1e6)

  // Every Mass unit is a positive multiple of kilograms, so the sign of the SI value is the sign of the value.
  property("Mass refines with Positive exactly when the value, in any unit, is > 0") =
    forAll(values, Gen.oneOf(Mass.units.toSeq)) { (v, unit) =>
      unit(v).refineEither[Positive].isRight == (v > 0)
    }

  // Seconds per unit, written by hand.
  private def seconds(v: Double, unit: squants.UnitOfMeasure[Time]): Double = unit match {
    case Nanoseconds => v * 1e-9
    case Microseconds => v * 1e-6
    case Milliseconds => v * 1e-3
    case Seconds => v
    case Minutes => v * 60
    case Hours => v * 3600
    case Days => v * 86400
    case EarthYears => v * 365.2421897 * 86400
    case EarthMegaYears => v * 1e6 * 365.2421897 * 86400
    case EarthGigaYears => v * 1e9 * 365.2421897 * 86400
    case other => throw new IllegalStateException(s"unexpected Time unit $other")
  }

  property("Time refines with Interval.Closed[0, 60] exactly when it is within 0 to 60 seconds") =
    forAll(values, Gen.oneOf(Time.units.toSeq)) { (v, unit) =>
      val s = seconds(v, unit)
      unit(v).refineEither[Interval.Closed[0, 60]].isRight == (s >= 0 && s <= 60)
    }

  // The value at which each scale passes through zero kelvin, written by hand.
  property("Temperature refines with Positive exactly when it is above absolute zero, on every scale") =
    forAll(values, Gen.oneOf(Temperature.units.toSeq)) { (v, scale) =>
      val aboveAbsoluteZero = scale match {
        case Kelvin => v > 0
        case Rankine => v > 0
        case Celsius => v > -273.15
        case Fahrenheit => v > -459.67
        case other => throw new IllegalStateException(s"unexpected Temperature scale $other")
      }
      scale(v).refineEither[Positive].isRight == aboveAbsoluteZero
    }

  property("refineOption and refineEither agree") = forAll(values, Gen.oneOf(Mass.units.toSeq)) { (v, unit) =>
    val q = unit(v)
    q.refineOption[Positive].isDefined == q.refineEither[Positive].isRight
  }
}
