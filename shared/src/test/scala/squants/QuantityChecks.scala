/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2013-2015, Gary Keorkunian                                       **
**                                                                      **
\*                                                                      */

package squants

import org.scalacheck.Gen

/**
 * @author  garyKeorkunian
 * @since   0.1
 *
 */
trait QuantityChecks {

  type TestData = Int
  val posNum: Gen[TestData] = Gen.posNum[TestData]
  val tol = 1e-13
  implicit val tolTime: Time = Seconds(tol)

  /** Double arithmetic, so equality is to a relative tolerance. */
  def close(a: Double, b: Double): Boolean = math.abs(a - b) <= 1e-9 * math.max(math.abs(a), math.abs(b))
}
