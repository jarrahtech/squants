package squants.iron.spec

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.*
import org.scalacheck.Prop.forAll
import org.scalacheck.{Gen, Properties}
import squants.iron.given
import squants.mass.{Kilograms, Mass}
import squants.space.{Length, Meters}
import squants.time.{Seconds, Time}

object QuantityConstraintChecks extends Properties("QuantityConstraint") {

  private val values: Gen[Double] = Gen.choose(-1e6, 1e6)
  private val massUnits = Gen.oneOf(Mass.units.toSeq)
  private val lengthUnits = Gen.oneOf(Length.units.toSeq)
  private val timeUnits = Gen.oneOf(Time.units.toSeq)

  property("Mass refines with Positive exactly when the kilogram value is > 0") = forAll(values, massUnits) { (v, unit) =>
    val q = unit(v)
    q.refineEither[Positive].isRight == (q.to(Kilograms) > 0)
  }

  property("Length refines with Less[100] exactly when the metre value is < 100") = forAll(values, lengthUnits) { (v, unit) =>
    val q = unit(v)
    q.refineEither[Less[100]].isRight == (q.to(Meters) < 100)
  }

  property("Time refines with Interval.Closed[0, 60] exactly when the second value is in [0, 60]") = forAll(values, timeUnits) { (v, unit) =>
    val q = unit(v)
    val s = q.to(Seconds)
    q.refineEither[Interval.Closed[0, 60]].isRight == (s >= 0 && s <= 60)
  }

  property("refineOption and refineEither agree") = forAll(values, massUnits) { (v, unit) =>
    val q = unit(v)
    q.refineOption[Positive].isDefined == q.refineEither[Positive].isRight
  }
}
