/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2013-2015, Gary Keorkunian                                       **
**                                                                      **
\*                                                                      */

package squants

import squants.time.Frequency

/*
 * ==Squants==
 * The Scala API for Quantities, Units of Measure and Dimensional Analysis
 *
 * ==Overview==
 * Squants is a framework of data types and a domain specific language (DSL) for representing Quantities,
 * their Units of Measure, and their Dimensional relationships.
 * The API supports typesafe dimensional analysis, improved domain models and more.
 * All types are immutable and thread-safe.
 *
 * Type and value aliases for common usages, and the extension that lets a number lead an expression
 *
 * @author  garyKeorkunian
 * @version 0.1
 * @since   0.1
 *
 */

type QuantitySeries[A <: Quantity[A]] = IndexedSeq[QuantityRange[A]]

/* Quantity Types brought into scope with just squants._ */

/* SI Base Quantities and their Base Units */
type Length = squants.space.Length
val Meters = squants.space.Meters
type Mass = squants.mass.Mass
val Kilograms = squants.mass.Kilograms
type Time = squants.time.Time
val Seconds = squants.time.Seconds
type ElectricCurrent = squants.electro.ElectricCurrent
val Amperes = squants.electro.Amperes
type Temperature = squants.thermal.Temperature
val Kelvin = squants.thermal.Kelvin
type ChemicalAmount = squants.mass.ChemicalAmount
val Moles = squants.mass.Moles
type LuminousIntensity = squants.photo.LuminousIntensity
val Candelas = squants.photo.Candelas

/* Common Derived Quantities */
type Angle = squants.space.Angle
val Radians = squants.space.Radians
type SolidAngle = squants.space.SolidAngle
val SquareRadians = squants.space.SquaredRadians

type Area = squants.space.Area
type Volume = squants.space.Volume

type Density = squants.mass.Density

type Velocity = squants.motion.Velocity
type Acceleration = squants.motion.Acceleration
type Jerk = squants.motion.Jerk
type Momentum = squants.motion.Momentum
type Force = squants.motion.Force
type GravitationalParameter = squants.motion.GravitationalParameter
type MassFlow = squants.motion.MassFlow
type VolumeFlow = squants.motion.VolumeFlow

type Energy = squants.energy.Energy
type Power = squants.energy.Power
type PowerRamp = squants.energy.PowerRamp

/* Market Quantities */
type Money = squants.market.Money
type Price[A <: Quantity[A]] = squants.market.Price[A]

/**
 * Lets a number lead in * and / by Time operations
 * {{{
 *    1.5 * Kilometers(10) should be(Kilometers(15))
 *    5 * Kilometers(10) should be(Kilometers(50))
 * }}}
 *
 * One extension over a union: separate extensions for Int, Long and Double would be ambiguous for an Int or a Long,
 * which also widen to the larger types.
 */
extension (n: Int | Long | Double | BigDecimal) {
  def *[A <: Quantity[A]](that: A): A = that * leadingDouble(n)
  def *[A](that: SVector[A]): SVector[A] = that * leadingDouble(n)
  def *[A <: Quantity[A]](that: Price[A]): Price[A] = that * leadingDouble(n)
  def /(that: Time): Frequency = Each(leadingDouble(n)) / that
  infix def per(that: Time): Frequency = n / that
}

private def leadingDouble(n: Int | Long | Double | BigDecimal): Double = n match {
  case d: Double => d
  case i: Int => i.toDouble
  case l: Long => l.toDouble
  case bd: BigDecimal => bd.toDouble
}
