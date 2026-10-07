/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2013-2015, Gary Keorkunian                                       **
**                                                                      **
\*                                                                      */

package squants

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class DimensionSpec extends AnyFlatSpec with Matchers {

  "Dimensions" should "have a stable hashCode" in {
    space.Length.hashCode.toHexString should be("29d67c0b")
  }

  they should "not share a symbol between two of their units" in {
    for (dimension <- Seq[Dimension[?]](mass.Mass, space.Length, mass.Density, motion.Velocity, time.Time))
      withClue(dimension.name) { dimension.units.map(_.symbol).size should be(dimension.units.size) }
  }
}
