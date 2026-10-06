package squants.iron.spec

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.*
import org.scalacheck.Prop.forAll
import org.scalacheck.{Gen, Properties}
import squants.iron.*
import squants.iron.given
import squants.mass.{Grams, Kilograms, Mass, Ounces, Pounds, Tonnes}
import squants.{UnitConverter, UnitOfMeasure}

object RefinedLiteralChecks extends Properties("RefinedLiteral") {

  private val massUnits: Gen[UnitOfMeasure[Mass] & UnitConverter] = Gen.oneOf(Kilograms, Grams, Tonnes, Pounds, Ounces)

  // Whatever refined's sign constraint accepts for a number must also hold for the quantity in the SI unit.
  property("a Positive number gives a quantity that passes the runtime Positive check, in any unit") =
    forAll(Gen.choose(1e-6, 1e6), massUnits) { (v, unit) =>
      val q: Mass = unit.refined(v.refineUnsafe[Positive])
      q.refineEither[Positive].isRight
    }

  property("a Negative number gives a quantity that passes the runtime Negative check, in any unit") =
    forAll(Gen.choose(-1e6, -1e-6), massUnits) { (v, unit) =>
      val q: Mass = unit.refined(v.refineUnsafe[Negative])
      q.refineEither[Negative].isRight
    }
}
