package squants

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import squants.energy.Watts
import squants.mass.{EarthMasses, Kilograms, SolarMasses}
import squants.space.{AstronomicalUnits, EarthRadii, NominalSolarRadii}
import squants.thermal.JoulesPerKelvin

class PhysicalConstantsSpec extends AnyFlatSpec with Matchers {

  import PhysicalConstants._

  behavior of "PhysicalConstants"

  it should "hold the CODATA 2018 values" in {
    G.value should be(6.67430e-11)
    StefanBoltzmann should be(5.670374419e-8)
    Boltzmann should be(JoulesPerKelvin(1.380649e-23))
  }

  it should "give a GravitationalParameter when the gravitational constant multiplies a Mass" in {
    (G * Kilograms(1)).toCubicMetersPerSecondSquared should be(6.67430e-11)
  }

  behavior of "Typed gravity and blackbody maths against reference values"

  private val earthMu = G * EarthMasses(1)

  it should "give Earth's surface gravity of about 9.82 m/s² from its mass and radius" in {
    val g = earthMu / (EarthRadii(1) * EarthRadii(1))
    g.toMetersPerSecondSquared should be(9.82 +- 0.01)
  }

  it should "give Earth's escape velocity of about 11.19 km/s" in {
    val v = ((earthMu / EarthRadii(1)) * 2).squareRoot
    v.toKilometersPerSecond should be(11.19 +- 0.005)
  }

  it should "give Earth's orbital period of about 365.25 days from 1 AU and one solar mass" in {
    val mu = G * SolarMasses(1)
    val a = AstronomicalUnits(1)
    val periodSquared = (a * a * a) / mu
    val period = periodSquared.squareRoot * (2 * math.Pi)
    period.toDays should be(365.25 +- 0.1)
  }

  it should "give the Sun's 5772 K from its luminosity and radius" in {
    val luminosity = Watts(3.828e26)
    val surface = NominalSolarRadii(1) * NominalSolarRadii(1) * (4 * math.Pi)
    (luminosity / surface).blackbodyTemperature.toKelvinScale should be(5772.0 +- 1.0)
  }
}
