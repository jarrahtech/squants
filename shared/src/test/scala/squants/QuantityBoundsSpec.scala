package squants

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import squants.mass.{Grams, Kilograms}
import squants.space.Meters

class QuantityBoundsTest extends AnyFlatSpec with Matchers {

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
    an[IllegalArgumentException] should be thrownBy QuantityBounds(Meters(2), Meters(1))
  }

  it should "know if it is a point (ie lower should be(upper)" in {
    bds1.isPoint should be(false)
    bds2.isPoint should be(true)
  }

  it should "handle map" in {
    bds1.map(_+Kilograms(1)) should be(QuantityBounds(Kilograms(4), Kilograms(6)))
  }

  it should "handle shift" in {
    val bds1 = QuantityBounds(Kilograms(3), Kilograms(5))
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

  it should "lerp" in {
    bds1.lerp(0.1) should be(Kilograms(3.2))
    bds1.mid should be(Kilograms(4))
    bds2.mid should be(Meters(1))
  }

  it should "no longer have the aliases that look like mutation or clash with Quantity's +-" in {
    assertDoesNotCompile("bds1 += Kilograms(1)")
    assertDoesNotCompile("bds1 -= Kilograms(1)")
    assertDoesNotCompile("bds1 +- Kilograms(1)")
    // the clearer aliases and all the named methods remain
    (bds1 ++ Kilograms(1)) should be(bds1.shift(Kilograms(1)))
    (bds1 =+ Kilograms(1)) should be(bds1.shiftUpper(Kilograms(1)))
    (bds1 -+ Kilograms(1)) should be(bds1.expand(Kilograms(1)))
  }

  it should "show that contains excludes both ends and includes counts them" in {
    // 3 and 5 are the ends of bds1
    val points = Seq(2.0, 3.0, 3.5, 4.0, 5.0, 6.0).map(Kilograms(_))
    for (p <- points) {
      bds1.includes(p) should be(p >= Kilograms(3) && p <= Kilograms(5))
      bds1.contains(p) should be(bds1.includes(p) && p != bds1.lower && p != bds1.upper)
    }
    bds1.includes(bds1.lower) should be(true)
    bds1.contains(bds1.lower) should be(false)
    bds1.includes(bds1.upper) should be(true)
    bds1.contains(bds1.upper) should be(false)
    // a point contains nothing, not even itself, but includes itself
    bds2.contains(bds2.lower) should be(false)
    bds2.includes(bds2.lower) should be(true)
  }

  it should "clamp a value into the bounds" in {
    bds1.clamp(Kilograms(2)) should be(Kilograms(3))
    bds1.clamp(Kilograms(3)) should be(Kilograms(3))
    bds1.clamp(Kilograms(4)) should be(Kilograms(4))
    bds1.clamp(Kilograms(5)) should be(Kilograms(5))
    bds1.clamp(Kilograms(6)) should be(Kilograms(5))
    bds2.clamp(Meters(7)) should be(Meters(1))
    bds2.clamp(Meters(-7)) should be(Meters(1))
    // a value in another unit is compared in its own unit
    bds1.clamp(Grams(6000)).toKilograms should be(5.0)
    bds1.clamp(Grams(4000)).toKilograms should be(4.0)
    bds1.clamp(Grams(1000)).toKilograms should be(3.0)
  }

  it should "take a plain A in clamp and ratio, like the other methods" in {
    // a value that is only known as a Quantity[Mass], not as a Mass, is not accepted
    assertDoesNotCompile("val q: squants.Quantity[squants.mass.Mass] = Kilograms(4); bds1.clamp(q)")
    assertDoesNotCompile("val q: squants.Quantity[squants.mass.Mass] = Kilograms(4); bds1.ratio(q)")
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

  it should "lerp and mid are consistent with ratio" in {
    bds1.lerp(0.0) should be(Kilograms(3))
    bds1.lerp(1.0) should be(Kilograms(5))
    bds1.lerp(0.5) should be(bds1.mid)
    bds1.lerp(1.5) should be(Kilograms(6))
    bds1.lerp(-0.5) should be(Kilograms(2))
    bds1.ratio(bds1.lerp(0.25)) should be(0.25)
    bds2.lerp(0.7) should be(Meters(1))
  }

  it should "say why it rejects inverted bounds" in {
    val e = the[IllegalArgumentException] thrownBy QuantityBounds(Meters(2), Meters(1))
    e.getMessage should include("Lower bound must be equal or smaller than upper")
    // equal bounds are allowed: that is a point
    QuantityBounds(Meters(1), Meters(1)).isPoint should be(true)
  }

  it should "convert from a QuantityRange" in {
    QuantityBounds.fromRange(QuantityRange(Kilograms(3), Kilograms(5))) should be(bds1)
  }

  it should "convert to a QuantityRange, except a point, which a range cannot represent" in {
    bds1.toRange should be(Some(QuantityRange(Kilograms(3), Kilograms(5))))
    bds2.toRange should be(None)
  }

  it should "round trip between QuantityBounds and QuantityRange" in {
    val range = QuantityRange(Meters(2), Meters(9))
    QuantityBounds.fromRange(range).toRange should be(Some(range))
    bds1.toRange.map(QuantityBounds.fromRange) should be(Some(bds1))
  }
}
