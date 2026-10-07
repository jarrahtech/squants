package squants

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import squants.mass.{Grams, Kilograms}
import squants.space.Meters

class QuantityBoundsSpec extends AnyFlatSpec with Matchers {

  behavior of "QuantityBounds"

  val bds1 = QuantityBounds(Kilograms(3), Kilograms(5))
  val bds2 = QuantityBounds(Meters(1), Meters(1))
  
  it should "create a range with lower and upper bound" in {
    bds1.lower should be(Kilograms(3))
    bds1.upper should be(Kilograms(5))
    bds2.lower should be(Meters(1))
    bds2.upper should be(Meters(1))
  }

  it should "throw an IllegalArgumentException when the lower bound > upper bound" in {
    val e = the[IllegalArgumentException] thrownBy QuantityBounds(Meters(2), Meters(1))
    e.getMessage should include("Lower bound must be equal or smaller than upper")
  }

  it should "know if it is a point (ie lower == upper)" in {
    bds1.isPoint should be(false)
    bds2.isPoint should be(true)
  }

  it should "handle map" in {
    bds1.map(_+Kilograms(1)) should be(QuantityBounds(Kilograms(4), Kilograms(6)))
  }

  it should "handle shift" in {
    bds1.shift(Kilograms(1)) should be(QuantityBounds(Kilograms(4), Kilograms(6)))
    (bds1 ++ Kilograms(31)) should be(QuantityBounds(Kilograms(34), Kilograms(36)))
    (bds1 -- Kilograms(1)) should be(QuantityBounds(Kilograms(2), Kilograms(4)))
  }

  it should "shiftUpper" in {
    bds2.shiftUpper(Meters(1)) should be(QuantityBounds(Meters(1), Meters(2)))
    (bds2 =+ Meters(2)) should be(QuantityBounds(Meters(1), Meters(3)))
    (bds1 =- Kilograms(1)) should be(QuantityBounds(Kilograms(3), Kilograms(4)))
  }

  it should "shiftLower" in {
    bds1.shiftLower(Kilograms(1)) should be(QuantityBounds(Kilograms(4), Kilograms(5)))
    bds2.shiftLower(Meters(-1)) should be(QuantityBounds(Meters(0), Meters(1)))
  }

  it should "expand" in {
    bds1.expand(Kilograms(1)) should be(QuantityBounds(Kilograms(2), Kilograms(6)))
    (bds2 -+ Meters(1)) should be(QuantityBounds(Meters(0), Meters(2)))
  }

  it should "shrink" in {
    bds1.shrink(Kilograms(1)) should be(QuantityBounds(Kilograms(4), Kilograms(4)))
  }

  it should "containsPoint" in {
    bds1.contains(Kilograms(2)) should be(false)
    bds1.contains(Kilograms(3)) should be(false)
    bds1.contains(Kilograms(4)) should be(true)
    bds1.contains(Kilograms(5)) should be(false)
    bds1.contains(Kilograms(6)) should be(false)

    bds2.contains(Meters(1)) should be(false)
  }

  it should "containsBounds" in {
    bds1.contains(QuantityBounds(Kilograms(2), Kilograms(6))) should be(false)
    bds1.contains(QuantityBounds(Kilograms(3), Kilograms(5))) should be(false)
    bds1.contains(QuantityBounds(Kilograms(4), Kilograms(4))) should be(true)

    bds2.contains(QuantityBounds(Meters(1), Meters(1))) should be(false)
  }

  it should "includesPoint" in {
    bds1.includes(Kilograms(2)) should be(false)
    bds1.includes(Kilograms(3)) should be(true)
    bds1.includes(Kilograms(4)) should be(true)
    bds1.includes(Kilograms(5)) should be(true)
    bds1.includes(Kilograms(6)) should be(false)

    bds2.includes(Meters(1)) should be(true)
  }

  it should "includesBounds" in {
    bds1.includes(QuantityBounds(Kilograms(2), Kilograms(6))) should be(false)
    bds1.includes(QuantityBounds(Kilograms(3), Kilograms(5))) should be(true)
    bds1.includes(QuantityBounds(Kilograms(4), Kilograms(4))) should be(true)

    bds2.includes(QuantityBounds(Meters(1), Meters(1))) should be(true)
    bds2.includes(QuantityBounds(Meters(0), Meters(1))) should be(false)
  }

  it should "toQuantity" in {
    bds1.toQuantity should be(Kilograms(2))
    bds2.toQuantity should be(Meters(0))
  }

  it should "toSeq" in {
    bds1.toSeq should be(Seq(Kilograms(3),Kilograms(5)))
    bds2.toSeq should be(Seq(Meters(1),Meters(1)))
  }

  it should "toList" in {
    bds1.toList should be(List(Kilograms(3),Kilograms(5)))
    bds2.toList should be(List(Meters(1),Meters(1)))
  }

  it should "mid" in {
    bds1.mid should be(Kilograms(4))
    bds2.mid should be(Meters(1))
  }

  it should "no longer offer the aliases that look like mutation or clash with Quantity's +-, or accept a plain Quantity" in {
    assertDoesNotCompile("bds1 += Kilograms(1)")
    assertDoesNotCompile("bds1 -= Kilograms(1)")
    assertDoesNotCompile("bds1 +- Kilograms(1)")
    // clamp and ratio take an A, like the other methods, not a Quantity[A]
    assertDoesNotCompile("val q: squants.Quantity[squants.mass.Mass] = Kilograms(4); bds1.clamp(q)")
    assertDoesNotCompile("val q: squants.Quantity[squants.mass.Mass] = Kilograms(4); bds1.ratio(q)")
  }

  it should "clamp a value into the bounds" in {
    bds1.clamp(Kilograms(2)) should be(Kilograms(3))
    bds1.clamp(Kilograms(4)) should be(Kilograms(4))
    bds1.clamp(Kilograms(6)) should be(Kilograms(5))
    bds2.clamp(Meters(7)) should be(Meters(1))
    // a value in another unit is compared in its own unit
    bds1.clamp(Grams(6000)).toKilograms should be(5.0)
    bds1.clamp(Grams(4000)).toKilograms should be(4.0)
  }

  it should "give the ratio of a value along the bounds" in {
    bds1.ratio(Kilograms(3)) should be(0.0)
    bds1.ratio(Kilograms(4)) should be(0.5)
    bds1.ratio(Kilograms(5)) should be(1.0)
    // outside the bounds the ratio extrapolates
    bds1.ratio(Kilograms(6)) should be(1.5)
    bds1.ratio(Kilograms(2)) should be(-0.5)
    // in another unit
    bds1.ratio(Grams(4000)) should be(0.5)
    // a point has no length, so the ratio is undefined: 0/0 and x/0
    bds2.ratio(Meters(1)).isNaN should be(true)
    bds2.ratio(Meters(2)).isInfinite should be(true)
  }

  it should "lerp, also outside 0 to 1, and invert ratio" in {
    bds1.lerp(0.0) should be(Kilograms(3))
    bds1.lerp(0.1) should be(Kilograms(3.2))
    bds1.lerp(1.0) should be(Kilograms(5))
    bds1.lerp(1.5) should be(Kilograms(6))
    bds1.lerp(-0.5) should be(Kilograms(2))
    bds1.ratio(bds1.lerp(0.25)) should be(0.25)
  }

  it should "convert to and from a QuantityRange, a point having no range" in {
    val range = QuantityRange(Kilograms(3), Kilograms(5))
    QuantityBounds.fromRange(range) should be(bds1)
    bds1.toRange should be(Some(range))
    bds2.toRange should be(None)
  }
}
