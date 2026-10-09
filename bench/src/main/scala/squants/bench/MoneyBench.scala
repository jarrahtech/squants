package squants.bench

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations.*

import squants.market.*

/** Money across currencies, with a direct exchange rate (USD/JPY) and with one found through USD (EUR/JPY) */
@State(Scope.Thread)
@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(2)
class MoneyBench {
  var context: MoneyContext =
    defaultMoneyContext.withExchangeRates(List(USD / JPY(100), USD / EUR(0.75), USD / GBP(0.6), XAU / USD(1200)))
  var dollars: Money = USD(125.5)
  var moreDollars: Money = USD(20)
  var yen: Money = JPY(9000)
  var euros: Money = EUR(80)
  var held: Set[Money] = Set(USD(1), USD(2), JPY(100), EUR(80), GBP(5))

  @Benchmark def plusSameCurrency: Money = dollars + moreDollars
  @Benchmark def convertDirectRate: Money = context.convert(yen, USD)
  @Benchmark def convertIndirectRate: Money = context.convert(yen, EUR)
  @Benchmark def indirectRateLookup: Option[CurrencyExchangeRate] = context.indirectRateFor(EUR, JPY)
  @Benchmark def compareDirectRate: Int = context.compare(dollars, yen)
  @Benchmark def compareIndirectRate: Int = context.compare(euros, yen)
  @Benchmark def currencyHash: Int = USD.hashCode
  @Benchmark def moneyHash: Int = dollars.hashCode
  @Benchmark def setLookup: Boolean = held.contains(euros)
}
