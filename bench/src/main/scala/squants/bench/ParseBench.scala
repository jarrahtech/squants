package squants.bench

import org.openjdk.jmh.annotations.Benchmark

import scala.util.Try

import squants.market.{ Money, MoneyContext, defaultMoneyContext }
import squants.space.Length
import squants.thermal.Temperature

/** Parsing quantities from strings */
class ParseBench extends SquantsBench {
  var lengthString: String = "12.5 km"
  var temperatureString: String = "300 K"
  var moneyString: String = "12.50 USD"
  var badString: String = "12.5 zz"
  var context: MoneyContext = defaultMoneyContext

  @Benchmark def length: Try[Length] = Length(lengthString)
  @Benchmark def lengthFailure: Try[Length] = Length(badString)
  @Benchmark def temperature: Try[Temperature] = Temperature(temperatureString)
  @Benchmark def money: Try[Money] = Money(moneyString)(using context)
  @Benchmark def moneyFailure: Try[Money] = Money(badString)(using context)
}
