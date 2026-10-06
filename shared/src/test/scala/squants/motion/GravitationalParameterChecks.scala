package squants.motion

import org.scalacheck.Prop.forAll
import org.scalacheck.{Gen, Properties}
import squants.QuantityChecks
import squants.energy.Grays
import squants.space.{CubicMeters, Meters, SquareMeters}

object GravitationalParameterChecks extends Properties("GravitationalParameter") with QuantityChecks {

  private val values: Gen[Double] = Gen.choose(1e-3, 1e9)

  property("(mu / area) * area = mu") = forAll(values, values) { (mu, area) =>
    val a = CubicMetersPerSecondSquared(mu) / SquareMeters(area)
    close((a * SquareMeters(area)).toCubicMetersPerSecondSquared, mu)
  }

  property("(mu / acceleration) * acceleration = mu") = forAll(values, values) { (mu, acc) =>
    val area = CubicMetersPerSecondSquared(mu) / MetersPerSecondSquared(acc)
    close((MetersPerSecondSquared(acc) * area).toCubicMetersPerSecondSquared, mu)
  }

  property("(mu / length) * length = mu") = forAll(values, values) { (mu, length) =>
    val energy = CubicMetersPerSecondSquared(mu) / Meters(length)
    close((energy * Meters(length)).toCubicMetersPerSecondSquared, mu)
  }

  property("(volume / mu) * mu = volume") = forAll(values, values) { (volume, mu) =>
    val ts = CubicMeters(volume) / CubicMetersPerSecondSquared(mu)
    close((CubicMetersPerSecondSquared(mu) * ts).toCubicMeters, volume)
  }

  property("squareRoot squares back to the specific energy") = forAll(values) { energy =>
    val v = Grays(energy).squareRoot.toMetersPerSecond
    close(v * v, energy)
  }
}
