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
  lazy val Kilo = 1024d
  lazy val Mega: Double = 1024d * Kilo
  lazy val Giga: Double = 1024d * Mega
  lazy val Tera: Double = 1024d * Giga
  lazy val Peta: Double = 1024d * Tera
  lazy val Exa: Double = 1024d * Peta
  lazy val Zetta: Double = 1024d * Exa
  lazy val Yotta: Double = 1024d * Zetta
}
