/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2013-2015, Gary Keorkunian                                       **
**                                                                      **
\*                                                                      */

package squants
package motion

/*
 * @author  garyKeorkunian
 * @since   0.1
 *
 */

type Distance = squants.space.Length
type DistanceUnit = squants.space.LengthUnit

lazy val SpeedOfLight: Velocity = Velocity(Meters(2.99792458e8), Seconds(1))

lazy val EquatorGravity: Acceleration = MetersPerSecondSquared(9.7903)
lazy val StandardEarthGravity: Acceleration = MetersPerSecondSquared(9.80665)
lazy val PoleGravity: Acceleration = MetersPerSecondSquared(9.8322)
