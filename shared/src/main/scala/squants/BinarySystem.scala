/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2013-2015, Gary Keorkunian                                       **
**                                                                      **
\*                                                                      */

package squants

/**
 * Singleton defining Metric System multipliers
 *
 * @author  garyKeorkunian
 * @since   0.1
 *
 */
object BinarySystem {
  val Kilo: Double = 1024d
  val Mega: Double = 1024d * Kilo
  val Giga: Double = 1024d * Mega
  val Tera: Double = 1024d * Giga
  val Peta: Double = 1024d * Tera
  val Exa: Double = 1024d * Peta
  val Zetta: Double = 1024d * Exa
  val Yotta: Double = 1024d * Zetta
}
