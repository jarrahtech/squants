package squants.motion

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import squants.QuantityParseException
import squants.energy.{Grays, Rads}
import squants.space.{CubicMeters, Meters, SquareMeters}
import squants.time.{Seconds, TimeSquared}

class GravitationalParameterSpec extends AnyFlatSpec with Matchers {

  behavior of "GravitationalParameter and its Units of Measure"

  it should "create values using UOM factories" in {
    CubicMetersPerSecondSquared(1).toCubicMetersPerSecondSquared should be(1)
    CubicKilometersPerSecondSquared(1).toCubicKilometersPerSecondSquared should be(1)
  }

  it should "create values from properly formatted Strings" in {
    GravitationalParameter("10.22 m³/s²").get should be(CubicMetersPerSecondSquared(10.22))
    GravitationalParameter("8.47 km³/s²").get should be(CubicKilometersPerSecondSquared(8.47))
    GravitationalParameter("10.22 zz").failed.get should be(QuantityParseException("Unable to parse GravitationalParameter", "10.22 zz"))
    GravitationalParameter("ZZ m³/s²").failed.get should be(QuantityParseException("Unable to parse GravitationalParameter", "ZZ m³/s²"))
  }

  it should "properly convert to all supported Units of Measure" in {
    val x = CubicMetersPerSecondSquared(1)
    x.toCubicMetersPerSecondSquared should be(1)
    x.toCubicKilometersPerSecondSquared should be(1e-9)

    val y = CubicKilometersPerSecondSquared(1)
    y.toCubicKilometersPerSecondSquared should be(1)
    y.toCubicMetersPerSecondSquared should be(1e9)
  }

  it should "return properly formatted strings for all supported Units of Measure" in {
    CubicMetersPerSecondSquared(1).toString(CubicMetersPerSecondSquared) should be("1.0 m³/s²")
    CubicKilometersPerSecondSquared(1).toString(CubicKilometersPerSecondSquared) should be("1.0 km³/s²")
  }

  it should "use cubic metres per second squared as its primary and SI unit" in {
    GravitationalParameter.primaryUnit should be(CubicMetersPerSecondSquared)
    GravitationalParameter.siUnit should be(CubicMetersPerSecondSquared)
    GravitationalParameter.units should be(Set(CubicMetersPerSecondSquared, CubicKilometersPerSecondSquared))
  }

  it should "return Acceleration when divided by Area" in {
    CubicMetersPerSecondSquared(100) / SquareMeters(25) should be(MetersPerSecondSquared(4))
  }

  it should "return Area when divided by Acceleration" in {
    CubicMetersPerSecondSquared(100) / MetersPerSecondSquared(4) should be(SquareMeters(25))
  }

  it should "return SpecificEnergy when divided by Length" in {
    CubicMetersPerSecondSquared(100) / Meters(4) should be(Grays(25))
    // a different unit on both sides still lands in J/kg
    (CubicKilometersPerSecondSquared(1) / Meters(1000)).toGrays should be(1e6)
  }

  it should "return Volume when multiplied by TimeSquared" in {
    CubicMetersPerSecondSquared(4) * (Seconds(5) * Seconds(5)) should be(CubicMeters(100))
  }

  it should "be the result of Volume divided by GravitationalParameter, as TimeSquared" in {
    val ts: TimeSquared = CubicMeters(100) / CubicMetersPerSecondSquared(4)
    ts.squareRoot should be(Seconds(5))
  }

  it should "be the result of Acceleration multiplied by Area" in {
    MetersPerSecondSquared(4) * SquareMeters(25) should be(CubicMetersPerSecondSquared(100))
  }

  it should "be the result of SpecificEnergy multiplied by Length" in {
    Grays(25) * Meters(4) should be(CubicMetersPerSecondSquared(100))
    // Rads are 0.01 Gy
    (Rads(2500) * Meters(4)).toCubicMetersPerSecondSquared should be(100.0 +- 1e-9)
  }

  it should "give a Velocity as the square root of a SpecificEnergy" in {
    Grays(25).squareRoot should be(MetersPerSecond(5))
    Rads(10000).squareRoot should be(MetersPerSecond(10))
    Grays(0).squareRoot should be(MetersPerSecond(0))
    Grays(-1).squareRoot.value.isNaN should be(true)
  }

  behavior of "GravitationalParameterConversions"

  it should "provide aliases for single unit values" in {
    import GravitationalParameterConversions._

    cubicMeterPerSecondSquared should be(CubicMetersPerSecondSquared(1))
    cubicKilometerPerSecondSquared should be(CubicKilometersPerSecondSquared(1))
  }

  it should "provide implicit conversion from Double" in {
    import GravitationalParameterConversions._

    val d = 10d
    d.cubicMetersPerSecondSquared should be(CubicMetersPerSecondSquared(d))
    d.cubicKilometersPerSecondSquared should be(CubicKilometersPerSecondSquared(d))
  }

  it should "provide Numeric support" in {
    import GravitationalParameterConversions.GravitationalParameterNumeric

    val gps = List(CubicMetersPerSecondSquared(100), CubicMetersPerSecondSquared(10))
    gps.sum should be(CubicMetersPerSecondSquared(110))

    // The first value ensures we get the sum in the unit we expect, whatever the Scala version
    val gpsKm = List(CubicMetersPerSecondSquared(0), CubicKilometersPerSecondSquared(1), CubicKilometersPerSecondSquared(2))
    gpsKm.sum should be(CubicMetersPerSecondSquared(3e9))
  }
}
