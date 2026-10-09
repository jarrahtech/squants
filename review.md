# Review findings

Three sets of findings are recorded here.

**Status (2026-10-09):** every item except 9, 13 and 28 is done (uncommitted) and marked DONE below; the JVM, JS and Native suites pass. The version is now 2.0.0, and the README release procedure was rewritten for GitHub Packages. Still open: 9, 13 and 28.

- **Part 1 — `/code-review` of `f1e8b59 simplify`** (2026-10-07, high effort, scope `origin/master...HEAD`): items 1–10. No correctness bugs; cleanup, documentation and consistency work. The JVM build compiled and the 7 touched specs passed (171 tests); JS and Native were not run.
- **Part 2 — `/scala-code-optimizer` audit of all main sources** (2026-10-07, 107 files under `*/src/main/`, Scala 3.8.4): items 11–27. Two confirmed bugs (11, 12), then performance and Scala 3 idiom suggestions. Tests were not audited. Items 4 and 9 were extended with what this pass found.

- **Part 3 — reported from mapemounde** (2026-10-07): item 28. A defect a consumer hit, traced back to work this fork planned and then skipped. Needs a decision.

What remains: 28 needs a decision and has already caused a downstream bug; 9 and 13 are performance items.

## Part 1: code review of `f1e8b59 simplify`

### 1. `toString(uom)` / `toTuple(uom)` not infix — DONE

- **Status:** done 2026-10-09. Both methods are `infix` and `QuantitySpec` uses them infix.
- **Where:** `shared/src/main/scala/squants/Quantity.scala:286` (`toString`) and `:307` (`toTuple`)
- **Problem:** The infix restoration skipped these two methods, but `README.md` lines 482-504 still show them in infix form (`load toString Kilowatts`, `load toTuple Megawatts`).
- **Failure:** A consumer copying `val kw = load toString Kilowatts` from the README into a build with `-deprecation -Werror` (this repo's own flags) gets "Alphanumeric method toString is not declared infix". The commit also rewrote the matching test to `x.toString(Kilothangs)` instead of restoring infix, so nothing exercises the documented form.
- **Fix:** Mark both `infix` and restore an infix test, or change the README to dot notation.

### 2. `QuantityRange.times` / `divide` not infix — DONE

- **Status:** done 2026-10-09. `times` and both `divide` overloads are `infix`, with infix tests in `QuantityRangeSpec`.
- **Where:** `shared/src/main/scala/squants/QuantityRange.scala:36`, `:57`, `:81`
- **Problem:** These were left non-infix while the same-named methods on `Quantity`, `SVector`, `Money` and `Price` were all marked `infix`.
- **Failure:** `q times 2.0` compiles cleanly, but `range times 2.0` or `range divide Meters(2)` raises the non-infix deprecation, which is an error under `-Werror`. The same commit uses `start to ...` infix inside `QuantityRange.divide`, so the API is inconsistent within one file.
- **Fix:** Mark them `infix`.

### 3. Stale `sbt tut` release step — DONE

- **Status:** done 2026-10-09. The `sbt tut` step is removed. The rest of the release procedure was later rewritten for the GitHub Packages publish workflow.
- **Where:** `README.md:1740`
- **Problem:** The commit deletes `shared/src/main/tut/README.md`, but the release procedure still says to "Build the README using tut" with `sbt tut`.
- **Failure:** A maintainer following the release steps runs `sbt tut`, which fails: there is no tut plugin in `project/plugins.sbt` and now no tut source either.
- **Fix:** Remove the step.

### 4. `Temperature.apply(String)` re-runs the regex per case — DONE

- **Status:** done 2026-10-09. The regex is a `private val` on `object Temperature` and is matched once per parse. The `Money.apply(String)` half is item 16, also done.
- **Where:** `shared/src/main/scala/squants/thermal/Temperature.scala:156`
- **Problem:** The flattened match re-runs the regex extractor once per case instead of once in total.
- **Failure:** Parsing "300 K" runs the regex 3 times, "500 R" 4 times, and any unparseable string 4 times, where the old code matched once and then switched on the captured unit. Bulk parsing costs up to 4x the matching work.
- **Fix:** A single `regex(value, unit)` followed by `unit.nn.toLowerCase match` keeps the flat shape with one match.
- **Added by the Scala audit:** the `Regex` itself is a local `val` inside `apply` (line 154), so the pattern is also recompiled on every call. Hoist it to a `private val` on `object Temperature`. Same problem, larger, in `Money.apply(String)` — see item 16.

### 5. `infix` is not enforced across overrides — DONE

- **Status:** done 2026-10-09. `MoneySpec` and `TemperatureSpec` use every overridden operator infix, and the README fork notes say overrides must repeat `infix`.
- **Where:** `shared/src/main/scala/squants/Quantity.scala:48`
- **Problem:** Scala 3.8.4 does not enforce `infix` agreement across overrides, so an override of the newly infix methods that omits the modifier silently loses infix use on the subtype.
- **Failure:** A scala-cli check on 3.8.4 showed `override def plus(that: A)` without `infix` compiles, but `sub plus x` on the subclass's static type then fails with "not declared infix". The in-repo overrides in `Money` and `Temperature` were all annotated by hand, but a future override or a downstream `Quantity` subclass that forgets it compiles and regresses with nothing to catch it.
- **Fix:** No compiler enforcement is available; options are a test that uses infix on each subtype, or a note in the contributor docs.

### 6. Symbol-uniqueness test hardcodes five dimensions — DONE

- **Status:** done 2026-10-09. New JVM-only `jvm/src/test/scala/squants/DimensionSymbolSpec.scala` scans the compiled classes and checks every dimension except `Money`. The five-dimension test was removed from the shared `DimensionSpec`, so JS and Native no longer run a symbol check.
- **Where:** `shared/src/test/scala/squants/DimensionSpec.scala:21`
- **Problem:** The consolidated "no two units share a symbol" test lists only `Mass`, `Length`, `Density`, `Velocity` and `Time`.
- **Failure:** `Power` gained `SolarLuminosities` (symbol `L☉`), which this commit's `PhysicalConstantsSpec` uses, but `Power` is not in the list. A duplicate symbol added to `Power`, `Energy` or any other dimension passes, and each new astronomical-unit dimension has to be added to the `Seq` by hand.
- **Fix:** Cover every dimension rather than a hand-maintained list.

### 7. Duplicate assertion in the `max` test — DONE

- **Status:** done 2026-10-09. The second assertion tests reversed operands, and the `min` title is corrected.
- **Where:** `shared/src/test/scala/squants/QuantitySpec.scala:405` and `:407`
- **Problem:** The rewritten test contains the identical assertion `(x max y) should be(Thangs(5))` twice.
- **Failure:** The second assertion adds no coverage. The neighbouring tests pair the named form with its operator alias, so this one could drop the duplicate or use it for the reversed operands (`y max x`).
- **Also:** The adjacent `min` test title says "return the greater of the two", which is wrong.

### 8. `DEFAULT_SBT_VERSION` fallback moved to 2.0.10 — DONE

- **Status:** done 2026-10-09. Decided 2026-10-09: sbt 1 repos are out of scope. The "any sbt project" claim was only in `vm_setup.md`, which has been deleted; the script itself makes no such claim, so it is unchanged.
- **Where:** `scripts/claude-cloud-setup.sh:26`
- **Problem:** The fallback moved from 1.13.0 to 2.0.10, although the script is documented as working for "any sbt project, not just this one".
- **Failure:** Running it in an sbt 1 repo that has no `project/build.properties` installs sbt 2.0.10; the build then loads under sbt 2 (Scala 3 build definitions, no sbt 1 plugins) and fails. The fallback is only safe for sbt 2 builds.
- **Fix:** Either narrow the documented claim or pick the fallback differently.

### 9. `QuantityBounds` memoisation half-converted

- **Where:** `shared/src/main/scala/squants/QuantityBounds.scala:45`
- **Problem:** `toSeq`, `toList`, `toTuple` and `mid` became `def`s, but `toQuantity` stays a `lazy val`.
- **Failure:** Every `QuantityBounds` still carries the lazy-val field and its synchronised initialisation for a single subtraction, while `mid` now recomputes `lower + toQuantity * 0.5` on every call.
- **Fix:** Make `toQuantity` a `def` too, or keep the derived values consistent.
- **Added by the Scala audit:** `QuantityRange` has the same pattern, unconverted: `lazy val inc` (`QuantityRange.scala:179`), `dec` (`:196`), `toQuantity` (`:314`), `toSeq` (`:320`) and `toList` (`:326`). Each is one or two additions or a two-element collection, yet every `QuantityRange` instance carries five extra fields plus the lazy-init state, and `divide` allocates one range per step. Turning them into `def`s is `needs-benchmark` only for callers that read `toQuantity` in a loop on one range (`times` reads it up to four times per element); elsewhere it is an allocation and footprint win. Removing public `lazy val`s is binary-incompatible.

### 10. Two stale build descriptions — DONE

- **Status:** done 2026-10-09. The `build.sbt` comment names the publish workflow. A generated JVM POM confirmed `scalajs-stubs` is gone, and the README now says so.
- **`build.sbt:7`:** The comment says the token comes from `GITHUB_TOKEN` "as CI sets it", but the test workflow no longer sets it (only `publish.yml` does).
- **`README.md:33`:** Says published POMs are "identical to the ones sbt 1 produced", but dropping the `scalajs-stubs % provided` dependency should remove that entry from the JVM POM. This was not checked against a generated POM.

## Part 2: Scala audit of main sources

Each item has: severity, location, original, proposed, rationale, performance classification, citation, caveats. Proposed snippets are suggestions and have **not** been applied; unless an item says "verified", they have not been compiled against the repo. `uncited` means no primary-source quote was looked up, with the reason given.

Sorted high → medium → low.

### 11. `timeToScalaDuration` throws `MatchError` for the Earth-year units — DONE

- **Status:** done 2026-10-09. Fallback case added as proposed, with a `TimeSpec` test. `TimeUnit` was not sealed. The caveats below still hold: large values throw `IllegalArgumentException`, and the fallback keeps fractional days while the original cases truncate.
- **Severity:** high (confirmed bug)
- **Location:** `shared/src/main/scala/squants/time/Time.scala:173-181`
- **Original:**
  ```scala
  implicit def timeToScalaDuration(time: Time): Duration = time.unit match {
    case Nanoseconds => Duration(time.value.toLong, NANOSECONDS)
    case Microseconds => Duration(time.value.toLong, MICROSECONDS)
    case Milliseconds => Duration(time.value.toLong, MILLISECONDS)
    case Seconds => Duration(time.value.toLong, SECONDS)
    case Minutes => Duration(time.value.toLong, MINUTES)
    case Hours => Duration(time.value.toLong, HOURS)
    case Days => Duration(time.value.toLong, DAYS)
  }
  ```
- **Proposed:**
  ```scala
  implicit def timeToScalaDuration(time: Time): Duration = time.unit match {
    case Nanoseconds => Duration(time.value.toLong, NANOSECONDS)
    // ... existing cases unchanged ...
    case Days => Duration(time.value.toLong, DAYS)
    case _ => Duration(time.toDays, DAYS)
  }
  ```
- **Rationale:** `TimeUnit` is not sealed, so the compiler gives no exhaustivity warning, and `EarthYears`, `EarthMegaYears` and `EarthGigaYears` have no case. Verified by running `val d: Duration = EarthYears(1)` against the main sources: `scala.MatchError: squants.time.EarthYears$`. `TimeSpec` only exercises the seven original units. Sealing `TimeUnit` would make the compiler catch the next omission.
- **Performance:** `equivalent` — one extra fall-through case.
- **Citation:** `uncited` — the behaviour was reproduced directly rather than taken from documentation.
- **Caveats:** `FiniteDuration` tops out near 292 years, so `EarthMegaYears`/`EarthGigaYears` and large `EarthYears` values will still throw, but as `IllegalArgumentException` from `Duration` rather than `MatchError`. Two related behaviours found in the same run and left alone here: the existing cases truncate (`Seconds(1.5)` becomes `1 second`), and `Time(Duration.Inf)` throws `IllegalArgumentException: unit not allowed on infinite Durations` (`Time.scala:68`). Sealing `TimeUnit` is source-breaking for downstream code that defines its own time units.

### 12. `Quantity.equals` is asymmetric and disagrees with `hashCode` — DONE

- **Status:** done 2026-10-09. Different-unit pairs compare in the primary unit; same-unit pairs compare raw values (the fast path). Tests added to `QuantitySpec`. This changes some cross-unit results: `Centimeters(70) == Meters(0.7)` was `true` (reverse `false`) and is now `false` both ways; `Yards(9) == Feet(27)` was `false` (reverse `true`) and is now `true` both ways.
- **Severity:** high (confirmed bug)
- **Location:** `shared/src/main/scala/squants/Quantity.scala:173-185`
- **Original:**
  ```scala
  override def equals(that: Any) = that match {
    case x: Quantity[_] if x.dimension == dimension => value == x.asInstanceOf[Quantity[A]].to(unit)
    case _ => false
  }

  override def hashCode() = {
    Objects.hash(dimension, Double.box(to(dimension.primaryUnit)))
  }
  ```
- **Proposed:**
  ```scala
  override def equals(that: Any): Boolean = that match {
    case x: Quantity[?] if x.dimension == dimension =>
      to(dimension.primaryUnit) == x.asInstanceOf[Quantity[A]].to(dimension.primaryUnit)
    case _ => false
  }

  override def hashCode(): Int = Objects.hash(dimension, Double.box(to(dimension.primaryUnit)))
  ```
- **Rationale:** `equals` converts the right-hand side into the left-hand side's unit, while `hashCode` converts into the primary unit; the two floating-point paths round differently. Verified over every pair of `Length` units and six values: 13 pairs were `==` with different hash codes (for example `3.0 µm` and `1.864109848492305E-9 mi`), and 1107 pairs gave `a == b` and `b == a` different answers. Quantities are therefore unreliable as `Set` members or `Map` keys across units. Comparing both sides in the primary unit makes `equals` symmetric and consistent with `hashCode` by construction.
- **Performance:** `needs-benchmark` — same-unit comparisons currently skip conversion entirely (`to` short-circuits when the unit matches) and would now convert both sides. A `this.unit == x.unit` fast path that compares `value` directly restores that and stays consistent.
- **Citation:** `uncited` — the `equals`/`hashCode` contract is a JVM (`java.lang.Object`) rule, not a Scala-specific one.
- **Caveats:** Changes equality semantics: some cross-unit pairs that are equal today become unequal and vice versa (same-unit comparisons are unaffected with the fast path). `Money` overrides both methods and is not affected. The `asInstanceOf` stays because the type argument is erased.

### 13. Every unit conversion boxes a `Double` and allocates a function

- **Severity:** high (performance, hot path)
- **Location:** `shared/src/main/scala/squants/UnitOfMeasure.scala:62`, `:72`, `:91`, `:97`, `:113`, `:119`; called from `Quantity.scala:262` and `:272`
- **Original:**
  ```scala
  final def convertTo[N](n: N)(implicit num: Numeric[N]) = converterTo(num.toDouble(n))
  final def convertFrom[N](n: N)(implicit num: Numeric[N]) = converterFrom(num.toDouble(n))

  // UnitConverter
  protected def converterTo: Double => Double = value => value / conversionFactor
  protected def converterFrom: Double => Double = value => value * conversionFactor
  ```
- **Proposed:**
  ```scala
  final def convertTo[N](n: N)(implicit num: Numeric[N]): Double = converterTo(num.toDouble(n))
  final def convertTo(n: Double): Double = converterTo(n)
  final def convertFrom[N](n: N)(implicit num: Numeric[N]): Double = converterFrom(num.toDouble(n))
  final def convertFrom(n: Double): Double = converterFrom(n)
  ```
- **Rationale:** `Quantity.to` and `Quantity.in` call `uom.convertTo(this.unit.convertFrom(value))` with a `Double`, but the only overload is generic in `N`, so the value is boxed to `java.lang.Double` and unboxed again through `Numeric`. This sits under every cross-unit `plus`, `compare`, `max`, `equals` and `toXxx`. A `Double` overload removes the box. Verified in isolation on 3.8.4 that the non-generic overload is selected for `Double`, `Int` and `Long` arguments and the generic one for `BigDecimal`.
- **Performance:** `needs-benchmark` — removes one box/unbox pair and one `Numeric` dispatch per conversion step, but HotSpot's escape analysis may already eliminate the box after inlining; JS and Native have no such optimisation, so the gain is likelier there. The second cost is untouched by this proposal: `converterTo`/`converterFrom` are `def`s returning a lambda, so each call allocates a fresh `Function1` (`Temperature.scala:182-205` does the same through eta-expansion). Fixing that needs the converters to be methods on `Double` or cached `val`s, which changes the `protected` contract units override.
- **Citation:** `uncited` — boxing under an erased type parameter is JVM behaviour; no Scala reference page states it in a quotable sentence.
- **Caveats:** Adding overloads is binary-compatible but can change overload selection for callers passing `Int`/`Long` (they now widen to `Double` instead of going through `Numeric`; the result is the same value). The same generic-`N` shape is in `UnitOfMeasure.apply[N]`, which every arithmetic result goes through (`unit(this.value + ...)`); that one is abstract and implemented per dimension, so it is a much larger change.

### 14. `SVector` operator aliases return a new function on every call — DONE

- **Status:** done 2026-10-09. The three aliases take their parameter and call the named method.
- **Severity:** high (performance; trivial fix)
- **Location:** `shared/src/main/scala/squants/SVector.scala:67`, `:75`, `:108`
- **Original:**
  ```scala
  def + = plus
  def - = minus
  def #* = crossProduct
  ```
- **Proposed:**
  ```scala
  def +(that: SVectorType): SVectorType = plus(that)
  def -(that: SVectorType): SVectorType = minus(that)
  def #*(that: DoubleVector): SVector[A] = crossProduct(that)
  ```
- **Rationale:** These are parameterless methods whose body eta-expands `plus`, so `a + b` is really `a.+.apply(b)`: it allocates a `Function1` and calls through it each time. The neighbouring aliases (`*`, `/`) already take the parameter directly. The inferred public type is also a function type rather than a method signature, which is what shows up in Scaladoc.
- **Performance:** `improved` — removes one closure allocation and one indirect call per vector addition, subtraction and cross product.
- **Citation:** "Scala 3 introduces Automatic Eta-Expansion which will deprecate the method to value syntax m _." — `docs.scala-lang.org/scala3/guides/migration/incompat-dropped-features.html`
- **Caveats:** Binary-incompatible (method signature changes). Source-compatible for `a + b`; code that used `v.+` as a function value still works through eta-expansion.

### 15. `Currency.apply` builds a `Failure` by throwing and catching — DONE

- **Status:** done 2026-10-09. Uses `toRight(...).toTry` with an explicit `Try[Currency]` result type.
- **Severity:** medium
- **Location:** `shared/src/main/scala/squants/market/Money.scala:456-459`
- **Original:**
  ```scala
  def apply(currency: String)(implicit fxContext: MoneyContext) = {
    fxContext.currencyMap.get(currency)
      .fold(Try[Currency](throw NoSuchCurrencyException(currency, fxContext)))(Success(_))
  }
  ```
- **Proposed:**
  ```scala
  def apply(currency: String)(implicit fxContext: MoneyContext): Try[Currency] =
    fxContext.currencyMap.get(currency).toRight(NoSuchCurrencyException(currency, fxContext)).toTry
  ```
- **Rationale:** The exception is thrown only so that `Try.apply` can catch it. `Option.toRight(...).toTry` produces the same `Failure` directly and states the result type, which is currently inferred.
- **Performance:** `improved` on the unknown-currency path (no throw/catch unwinding); `equivalent` on the success path. `toRight` takes its argument by name, so the exception is still only constructed on a miss.
- **Citation:** `uncited` — standard-library API usage; no reference page prescribes it.
- **Caveats:** none. The exception's stack trace is still captured at construction.

### 16. `Money.apply(String)` rebuilds and recompiles its regex on every call — DONE

- **Status:** done 2026-10-09. The pattern is a `private[market] lazy val moneyPattern` on `MoneyContext`, built with `mkString`. An empty currency set now gives a `Failure` instead of throwing; test added to `MoneySpec`.
- **Severity:** medium
- **Location:** `shared/src/main/scala/squants/market/Money.scala:407-413`
- **Original:**
  ```scala
  def apply(s: String)(implicit fxContext: MoneyContext): Try[Money] = {
    val regex = ("([-+]?[0-9]*\\.?[0-9]+) *(" + fxContext.currencies.map(_.code).reduceLeft(_ + "|" + _) + ")").r
    s match {
      case regex(value, currency) => Currency(currency.nn).map(Money(BigDecimal(value.nn), _))
      case _ => Failure(QuantityParseException("Unable to parse Money", s))
    }
  }
  ```
- **Proposed:**
  ```scala
  // in MoneyContext, next to currencyMap
  private[market] lazy val moneyPattern: Regex =
    ("([-+]?[0-9]*\\.?[0-9]+) *(" + currencies.map(_.code).mkString("|") + ")").r

  // in object Money
  def apply(s: String)(implicit fxContext: MoneyContext): Try[Money] = s match {
    case fxContext.moneyPattern(value, currency) => Currency(currency.nn).map(Money(BigDecimal(value.nn), _))
    case _ => Failure(QuantityParseException("Unable to parse Money", s))
  }
  ```
- **Rationale:** Each parse maps the whole currency set, concatenates roughly 30 codes and compiles a `Pattern`, all of which depend only on the `MoneyContext`. `MoneyContext` already caches `currencyMap` the same way, and `Dimension.QuantityString` (`Dimension.scala:76`) caches the equivalent regex for other quantities. `mkString` also fixes a crash: `reduceLeft` throws `UnsupportedOperationException` on a context with no currencies, outside the `Try`.
- **Performance:** `improved` — pattern compilation dominates the cost of a parse; here `lazy val` is the right tool (expensive, reused, possibly never needed).
- **Citation:** `uncited` — follows from `java.util.regex.Pattern` compilation cost, not a Scala-specific rule.
- **Caveats:** With an empty currency set the pattern becomes `(...) *()` and matching falls through to the `Failure` branch instead of throwing — a behaviour change, in the safer direction. `Dimension.scala:76` has the same `reduceLeft(_ + "|" + _)`; no current unit symbol contains a regex metacharacter (checked), but neither site quotes its alternatives.

### 17. `MoneyContext.indirectRateFor` recomputes its lookup sets for every currency — DONE

- **Status:** done 2026-10-09. The two currency sets are built once and `find` stops at the first match; the outer match is an `orElse`. Existing `MoneyContextSpec` cross-rate tests cover it.
- **Severity:** medium
- **Location:** `shared/src/main/scala/squants/market/MoneyContext.scala:70-91`
- **Original:**
  ```scala
  val curs = for {
    cur <- currencies
    if ratesWithCurA.map(_.base.currency).contains(cur) || ratesWithCurA.map(_.counter.currency).contains(cur)
    if ratesWithCurB.map(_.base.currency).contains(cur) || ratesWithCurB.map(_.counter.currency).contains(cur)
  } yield cur

  curs.headOption match {
    case Some(cur) => Some(CurrencyExchangeRate(convert(cur(1), curA), convert(cur(1), curB)))
    case None => None
  }
  ```
- **Proposed:**
  ```scala
  val withA = ratesWithCurA.iterator.flatMap(r => Iterator(r.base.currency, r.counter.currency)).toSet
  val withB = ratesWithCurB.iterator.flatMap(r => Iterator(r.base.currency, r.counter.currency)).toSet

  currencies.find(cur => withA(cur) && withB(cur))
    .map(cur => CurrencyExchangeRate(convert(cur(1), curA), convert(cur(1), curB)))
  ```
- **Rationale:** The guards rebuild up to four `Seq`s per currency, then scan them linearly, and the comprehension materialises every matching currency only to take the first. Building the two currency sets once and using `find` short-circuits at the first match. This runs on every cross-currency operation that has no direct rate.
- **Performance:** `improved` — from O(currencies × rates) allocation and scanning to O(currencies + rates), and it stops at the first hit.
- **Citation:** `uncited` — collection-API selection; the Scala collections overview does not state this in a quotable sentence.
- **Caveats:** `filter` then `headOption` and `find` both take the first match in the `Set`'s iteration order, so the chosen intermediate currency is unchanged. The outer `directRateFor(...) match { case Some(rate) => Some(rate) ... }` can be an `orElse` at the same time.

### 18. Comparisons convert the same operand twice — DONE

- **Status:** done 2026-10-09. All three `compare` methods convert once.
- **Severity:** medium
- **Location:** `shared/src/main/scala/squants/Quantity.scala:206`; `shared/src/main/scala/squants/AbstractQuantityNumeric.scala:39`; `shared/src/main/scala/squants/market/MoneyContext.scala:158-161`
- **Original:**
  ```scala
  def compare(that: A) = if (this.value > that.to(unit)) 1 else if (this.value < that.to(unit)) -1 else 0
  ```
- **Proposed:**
  ```scala
  def compare(that: A): Int = {
    val other = that.to(unit)
    if (this.value > other) 1 else if (this.value < other) -1 else 0
  }
  ```
- **Rationale:** `compare` backs `<`, `>`, sorting and `QuantityRange.contains`, and on the "not greater" path it performs the unit conversion twice. `AbstractQuantityNumeric.compare` calls `to(unit)` up to four times, and `MoneyContext.compare` runs a full `convert` (a rate lookup, possibly an indirect one) twice. Binding the converted value once is the whole fix.
- **Performance:** `improved` — halves the conversions on one branch (and with item 13 unfixed, halves the boxing and closure allocation too).
- **Citation:** `uncited` — common-subexpression elimination; nothing Scala-specific.
- **Caveats:** none. Keeping the explicit `>`/`<` form preserves the current NaN behaviour (`compare` returns 0); `java.lang.Double.compare` would not.

### 19. `implicit class` wrappers should be `extension` methods — DONE

- **Status:** done 2026-10-09. All 87 implicit classes are extension methods. Two designs differ from the proposal. (1) The four `SquantifiedXxx` classes became one extension on `Int | Long | Double | BigDecimal`, because separate extensions are ambiguous for an `Int` or `Long` receiver (`3 * Meters(1)` did not compile). (2) The Money DSL sits in `given MoneyDsl` inside `MoneyConversions`, because its methods are named after the currencies and a wildcard import would make `USD` ambiguous; it needs `import MoneyConversions.given`. A `lazy val kW` and an extension `kW` do coexist in one object, so the other conversions kept their wildcard imports.
- **Severity:** medium
- **Location:** 87 sites. The four in `shared/src/main/scala/squants/package.scala:86`, `:102`, `:118`, `:134` (`SquantifiedDouble`/`Long`/`Int`/`BigDecimal`) are not value classes; the per-dimension `XxxConversions[A](n: A)(implicit num: Numeric[A])` and `XxxStringConversions(s: String)` classes (for example `energy/Power.scala:128`, `:144`) cannot be, because of the implicit constructor parameter. Three already use `extends AnyVal`: `mass/MomentOfIntertia.scala:67`, `motion/Torque.scala:55`, `motion/AngularAcceleration.scala:102`.
- **Original:**
  ```scala
  implicit class SquantifiedDouble(d: Double) {
    def *[A <: Quantity[A]](that: A): A = that * d
    def *[A](that: SVector[A]): SVector[A] = that * d
    def *[A <: Quantity[A]](that: Price[A]): Price[A] = that * d
    def /(that: Time): Frequency = Each(d) / that
    infix def per(that: Time): Frequency = /(that)
  }
  ```
- **Proposed:**
  ```scala
  extension (d: Double) {
    def *[A <: Quantity[A]](that: A): A = that * d
    def *[A](that: SVector[A]): SVector[A] = that * d
    def *[A <: Quantity[A]](that: Price[A]): Price[A] = that * d
    def /(that: Time): Frequency = Each(d) / that
    infix def per(that: Time): Frequency = d / that
  }
  ```
- **Rationale:** Every `2.0 * Meters(3)` or `5.watts` currently allocates a wrapper object just to dispatch one method. Extension methods compile to static methods with no wrapper. A minimal check on 3.8.4 confirmed that an extension `*` on `Double` and `Int` is still selected when the built-in `*` overloads do not apply.
- **Performance:** `improved` for the 84 non-`AnyVal` classes (no wrapper allocation); `equivalent` for the three that are already value classes.
- **Citation:** "Extension methods have no direct counterpart in Scala 2, but they can be simulated with implicit classes." — `docs.scala-lang.org/scala3/reference/contextual/relationship-implicits.html`
- **Caveats:** Binary-incompatible, and source-incompatible for anyone naming the classes (`new PowerConversions(5)`). The conversions objects share a name with their implicit class (`object PowerConversions { implicit class PowerConversions ... }`), so user imports of `PowerConversions._` keep working but should be checked. Scala 2 cross-building is not a concern for this build.

### 20. `implicit` parameters, vals and objects should be `using` / `given` — DONE

- **Status:** done 2026-10-09. Every `(implicit ...)` parameter in main is `using`, the 73 Numeric objects are `given X: AbstractQuantityNumeric[T](unit) {}`, and `Dimension.dimensionImplicit` is a given. Tests that needed a Numeric import `XConversions.given`. README has a "Breaking changes in 2.0.0" section.
- **Severity:** medium
- **Location:** 231 `(implicit num: Numeric[N])` parameters across all dimensions; 73 `implicit object XxxNumeric` (for example `energy/Power.scala:148`); `(implicit context: MoneyContext)` and `(implicit tolerance: A)` parameters in `market/Money.scala` and `Quantity.scala:193-199`; `shared/src/main/scala/squants/Dimension.scala:87`
- **Original:**
  ```scala
  def apply[A](n: A)(implicit num: Numeric[A]) = Power(n, this)

  implicit object PowerNumeric extends AbstractQuantityNumeric[Power](Power.primaryUnit)
  ```
- **Proposed:**
  ```scala
  def apply[A](n: A)(using num: Numeric[A]): Power = Power(n, this)

  given PowerNumeric: AbstractQuantityNumeric[Power] = new AbstractQuantityNumeric[Power](Power.primaryUnit) {}
  ```
- **Rationale:** The build is Scala 3 only, and the `iron` module already uses `given`/`using`, so the main module is the inconsistent half. `using` makes the contextual nature of the argument visible at call sites that pass it explicitly, and `given` separates instance definitions from ordinary members.
- **Performance:** `equivalent` for parameters. For the instances, a `given` alias like the one above is initialised lazily on first use, as the `implicit object` is; the anonymous subclass adds one class per instance.
- **Citation:** "Given instances can be mapped to combinations of implicit objects, classes and implicit methods." — `docs.scala-lang.org/scala3/reference/contextual/relationship-implicits.html`
- **Caveats:** Source-incompatible in two ways: callers passing the argument explicitly must write `f(x)(using num)`, and wildcard imports stop bringing instances in (`import PowerConversions.*` no longer imports `PowerNumeric`; it needs `import PowerConversions.given`). That second point breaks every README example that relies on the Numeric instances. Binary-incompatible for the instances. Worth doing only as a deliberate, versioned API change.

### 21. `implicit def` conversions should be `Conversion` instances — DONE

- **Status:** done 2026-10-09. The three real conversions are `Conversion` givens; `MoneyConversions.fromLong` / `fromDouble` were removed as redundant. Use sites now need `import scala.language.implicitConversions`.
- **Severity:** medium
- **Location:** `shared/src/main/scala/squants/time/Time.scala:173`, `:183`; `shared/src/main/scala/squants/Dimensionless.scala:137`; `shared/src/main/scala/squants/market/Money.scala:503-504`
- **Original:**
  ```scala
  implicit def dimensionlessToDouble(d: Dimensionless): Double = d.toEach
  ```
- **Proposed:**
  ```scala
  given dimensionlessToDouble: Conversion[Dimensionless, Double] = _.toEach
  ```
- **Rationale:** These are the five places the library converts types implicitly, and each file carries `import scala.language.implicitConversions` to permit the Scala 2 form. `Conversion` is the Scala 3 encoding and makes the intent explicit. `Money.scala:503-504` exists only to feed `Long`/`Double` into the `MoneyConversions` implicit class, so it disappears if item 19 is done.
- **Performance:** `equivalent` — a `given` conversion is a cached instance whose `apply` is called at the conversion site.
- **Citation:** "Implicit conversions are done by creating instances of Conversion." — attributed by the skill's reference to the Scala 3 contextual abstractions docs, `docs.scala-lang.org/scala3/reference/contextual/conversions.html`; not re-checked against the live page.
- **Caveats:** Same import change as item 20: users need `import TimeConversions.given` (or the specific name). Item 11's bug lives in one of these conversions and should be fixed first.

### 22. `Currency.hashCode` allocates two collections per call — DONE

- **Status:** done 2026-10-09. `hashCode` is a cached `val` using `Objects.hash`. Hash values changed.
- **Severity:** medium
- **Location:** `shared/src/main/scala/squants/market/Money.scala:449-452`
- **Original:**
  ```scala
  override def hashCode(): Int = {
    val state = Seq(code, name, symbol, formatDecimals)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
  ```
- **Proposed:**
  ```scala
  override val hashCode: Int = java.util.Objects.hash(code, name, symbol, Int.box(formatDecimals))
  ```
- **Rationale:** `Currency` is the key of `MoneyContext.currencies` (a `Set`) and is hashed on every `Money.hashCode`. The current body builds a `Seq[Any]` (boxing `formatDecimals`), maps it to a second `Seq`, and folds — all over four immutable constructor fields. Currencies are a few dozen singletons, so caching the hash in a `val` costs nothing.
- **Performance:** `improved` — no allocation after construction. This is the opposite case to item 9: a value computed once per singleton and read often.
- **Citation:** `uncited` — allocation behaviour of `Seq.map`/`foldLeft`; not a documented language rule.
- **Caveats:** `Objects.hash` seeds with 1 rather than 0, so hash values change; that only matters if a hash was ever persisted. `Money.hashCode` (`Money.scala:266`) already uses `Objects.hash`. A `val` in an abstract class is initialised before subclass bodies run, which is safe here because all four inputs are constructor parameters.

### 23. Public members without explicit result types — DONE

- **Status:** done 2026-10-09. 1,876 result types added to public and protected members by a script that reads the compiler's inferred types from semanticdb, so each annotation is the type inferred before. Verified by comparing all 6,604 member signatures before and after: identical apart from 10 that name the same type through an alias. Not annotated: members whose body is a literal, a plain reference or `new` (`def primaryUnit = Watts`, `val symbol = "W"`), private members, and two `def *(that: Quantity[?])`-style methods whose type cannot be written in source.
- **Severity:** medium
- **Location:** throughout; the load-bearing ones are `shared/src/main/scala/squants/Quantity.scala:109`, `:119`, `:173`, `:183`, `:193-199`, `:206`, `:228`, `:242`, `:249`, `:270`; `UnitOfMeasure.scala:35`, `:62`, `:72`; `AbstractQuantityNumeric.scala:19-39`; `QuantityRange.scala:44-308`; `QuantityBounds.scala`; most of `market/Money.scala:90-395`. Every dimension repeats the pattern (`def toWatts = to(Watts)`, `def apply(value: Any) = parse(value)`, `def W = Watts(n)`).
- **Original:**
  ```scala
  infix def in(uom: UnitOfMeasure[A]) = uom match {
    case u if u == this.unit => this
    case _ => uom(uom.convertTo(this.unit.convertFrom(value)))
  }
  def /%(that: Double) = divideAndRemainder(that)
  ```
- **Proposed:**
  ```scala
  infix def in(uom: UnitOfMeasure[A]): A = uom match {
    case u if u == this.unit => this
    case _ => uom(uom.convertTo(this.unit.convertFrom(value)))
  }
  def /%(that: Double): (A, A) = divideAndRemainder(that)
  ```
- **Rationale:** For a published library the inferred type is the binary and source contract, so an innocent body edit can change it. Several of these infer something narrower or stranger than intended: `Dimension` members such as `def primaryUnit = Watts` infer the singleton type `Watts.type`, `UnitOfMeasure.unapply` infers `Some[Double]`, and the `SVector` aliases in item 14 infer function types.
- **Performance:** `equivalent` at runtime.
- **Citation:** `uncited` — a widely held library-design convention; the Scala 3 reference does not mandate it.
- **Caveats:** Where the inferred type is narrower than the natural annotation (`Watts.type` versus `PowerUnit`, `Some[Double]` versus `Option[Double]`), widening it is a source and binary change; `AbstractQuantityNumeric[Power](Power.primaryUnit)` relies on the narrow type satisfying `UnitOfMeasure[A] & PrimaryUnit`, and `Some` keeps unit extractors irrefutable. Annotate with the type that is inferred today unless a wider one is intended. Large and mechanical; start with the core files.

### 24. `Dimension.parseString` calls `.get` on a lookup the regex is assumed to guarantee — DONE

- **Status:** done 2026-10-09. Uses `map(...).toRight(...).toTry` instead of `.get`.
- **Severity:** low
- **Location:** `shared/src/main/scala/squants/Dimension.scala:69-74`
- **Original:**
  ```scala
  s match {
    case QuantityString(value, symbol) => Success(symbolToUnit(symbol.nn).get(BigDecimal(value.nn)))
    case _ => Failure(QuantityParseException(s"Unable to parse $name", s))
  }
  ```
- **Proposed:**
  ```scala
  s match {
    case QuantityString(value, symbol) =>
      symbolToUnit(symbol.nn)
        .map(_(BigDecimal(value.nn)))
        .toRight(QuantityParseException(s"Unable to parse $name", s))
        .toTry
    case _ => Failure(QuantityParseException(s"Unable to parse $name", s))
  }
  ```
- **Rationale:** The `.get` is safe only while the regex alternatives and `symbolToUnit` agree exactly, which holds because both are built from `units`. A unit symbol containing a regex metacharacter would break that silently and surface as `NoSuchElementException` from a method whose signature promises a `Try`. Staying inside `Option`/`Try` keeps the promise.
- **Performance:** `equivalent` — one extra small allocation on the success path of a parse that already runs a regex.
- **Citation:** `uncited` — `Option.get` avoidance is community guidance; the Scala 3 book page on functional error handling (`docs.scala-lang.org/scala3/book/functional-error-handling.html`) covers `Option` but was not quoted.
- **Caveats:** none.

### 25. `QuantityRange.divide` casts an empty `IndexedSeq` instead of typing it — DONE

- **Status:** done 2026-10-09. The cast is replaced with `IndexedSeq.empty[QuantityRange[A]]`.
- **Severity:** low
- **Location:** `shared/src/main/scala/squants/QuantityRange.scala:63`
- **Original:**
  ```scala
  accumulate(IndexedSeq.empty.asInstanceOf[QuantitySeries[A]], lower)
  ```
- **Proposed:**
  ```scala
  accumulate(IndexedSeq.empty[QuantityRange[A]], lower)
  ```
- **Rationale:** `QuantitySeries[A]` is an alias for `IndexedSeq[QuantityRange[A]]`, so the element type can simply be given. The cast is unchecked and would keep compiling if the alias changed to something an `IndexedSeq` is not.
- **Performance:** `equivalent` — both produce the shared empty `Vector`; the `@tailrec` shape of `accumulate` is untouched.
- **Citation:** `uncited` — typed-over-cast is general guidance; the nearest primary page is `docs.scala-lang.org/tour/pattern-matching.html`, which does not address this case.
- **Caveats:** none.

### 26. `Money.max` / `min` build a tuple to re-test a type that is already known — DONE

- **Status:** done 2026-10-09. Both match on `that.currency` alone.
- **Severity:** low
- **Location:** `shared/src/main/scala/squants/market/Money.scala:237-250`
- **Original:**
  ```scala
  override infix def max(that: Money): Money = (that, that.currency) match {
    case (m: Money, this.currency) => new Money(amount.max(m.amount))(currency)
    case _ => throw new UnsupportedOperationException("max not supported for cross-currency comparison - use moneyMax")
  }
  ```
- **Proposed:**
  ```scala
  override infix def max(that: Money): Money = that.currency match {
    case this.currency => new Money(amount.max(that.amount))(currency)
    case _ => throw new UnsupportedOperationException("max not supported for cross-currency comparison - use moneyMax")
  }
  ```
- **Rationale:** `that` is statically a `Money`, so the `m: Money` type test can never fail and the tuple exists only to carry it. `plus`, `minus`, `divide` and `compare` in the same class already match on `that.currency` alone.
- **Performance:** `improved` marginally — no `Tuple2` allocation per call.
- **Citation:** `uncited` — follows from the static type; nothing to cite.
- **Caveats:** none. Separately, these methods (and `plus`, `minus`, `divide`, `compare`, plus `MoneyContext.convert` and `CurrencyExchangeRate.convert`) throw for mixed currencies from signatures that look total. That is inherited from `Quantity`'s contract and documented, so it is noted rather than raised as its own item.

### 27. Eleven `package object`s could be top-level definitions — DONE

- **Status:** done 2026-10-09. The five empty package objects were deleted and the other six are top-level definitions (type and value aliases kept as they were, not `export`). The package doc comments on `squants` and `squants.market` are now plain comments, since a package has nowhere else to carry scaladoc.
- **Severity:** low
- **Location:** `shared/src/main/scala/squants/package.scala:28`; `market/package.scala:56`; and `package.scala:16` in each of `time`, `photo`, `radio`, `electro`, `mass`, `energy`, `thermal`, `motion`, `space`
- **Original:**
  ```scala
  package object squants {
    type QuantitySeries[A <: Quantity[A]] = IndexedSeq[QuantityRange[A]]
    type Length = squants.space.Length
    val Meters = squants.space.Meters
    // ...
  }
  ```
- **Proposed:**
  ```scala
  package squants

  type QuantitySeries[A <: Quantity[A]] = IndexedSeq[QuantityRange[A]]
  export squants.space.{Length, Meters}
  // ...
  ```
- **Rationale:** Scala 3 allows types, values and extensions at the top level of a package, so the wrapper object is no longer needed. The `type X = pkg.X` / `val X = pkg.X` alias pairs are exactly what an `export` clause expresses in one line.
- **Performance:** `equivalent` at runtime.
- **Citation:** "If you're familiar with Scala 2, this approach replaces package objects." — `docs.scala-lang.org/scala3/book/taste-toplevel-definitions.html`
- **Caveats:** Binary-incompatible: top-level definitions compile into a synthetic `package$package` object rather than `package$`. Whether `export` forwards both the type and the companion-like unit object the way the paired aliases do was not checked. Purely cosmetic; do it only alongside another breaking release.

### Audited and deliberately not raised

- **`sealed trait TemperatureScale` → `enum`:** the scales are objects with behaviour that mix in `PrimaryUnit`/`SiBaseUnit`, and every other dimension uses the same open-trait-plus-objects shape. An `enum` would make `Temperature` the odd one out.
- **The 500+ `lazy val`s in `XxxConversions` objects** (`lazy val watt = Watts(1)`): these live on singletons, are created at most once, and laziness avoids initialisation-order cycles between dimension objects. Not the per-instance case of item 9.
- **`DoubleVector(coordinates: Double*)`:** a varargs `Seq[Double]` boxes every coordinate, and `plus`/`minus`/`dotProduct` allocate a tuple per element through `zipAll`. Fixing it means an `Array[Double]`-backed vector with hand-written equality, which is a redesign rather than a refactor. `QuantityVector` with no coordinates throws `ArrayIndexOutOfBoundsException` from `valueUnit` (confirmed).
- **`Temperature.convert`** (`thermal/Temperature.scala:91-122`): matches on a freshly allocated `Tuple3` per conversion; likely scalar-replaced on HotSpot, and the table form is the readable one.
- **Not present:** `var`, `null`, `return`, `catch`, `Await`, `Future`, `scala.Enumeration`, `extends App`, `do`/`while`, procedure syntax, `m _`, `.size == 0` on lists, or `filter(...).head` chains in main sources. The one recursive helper (`QuantityRange.divide`) is already `@tailrec`.

## Part 3: reported from mapemounde

### 28. `Temperature / Temperature` and `Temperature * Double` work in the left operand's scale

- **Severity:** high (silently wrong numbers; has already produced a bug in a consumer)
- **Where:** `shared/src/main/scala/squants/Quantity.scala:82` (`divide(that: A)`, `this.value / that.to(unit)`), `:64` (`times(that: Double)`) and `:74` (`divide(that: Double)`), all inherited unchanged by `shared/src/main/scala/squants/thermal/Temperature.scala`. `Temperature` overrides `plus` and `minus` but not these.
- **Problem:** A ratio of two temperatures, or a temperature scaled by a number, only means something on an absolute scale. The inherited operators use whatever scale the left operand happens to be stored in, so the answer depends on the unit, not on the physical temperature.
- **Failure:** the same two temperatures, 293.15 K and 283.15 K, give three different ratios:
  ```scala
  Kelvin(293.15) / Kelvin(283.15)   // 1.0353  correct
  Kelvin(293.15) / Celsius(10)      // 1.0353  correct, the right operand is converted to Kelvin
  Celsius(20)    / Celsius(10)      // 2.0     wrong
  Celsius(20)    / Kelvin(283.15)   // 2.0     wrong, the right operand is converted to Celsius
  Celsius(10) * 2                   // Celsius(20) = 293.15 K, not 566.3 K
  ```
  These values are read off the code, not run here. The first and third forms were confirmed by failing tests in mapemounde against 1.8.4 (below).
- **History:** this was planned and then dropped.
  - The fork plan (`squants.md` in mapemounde, Task 3 item 1) said: "`Temperature / Temperature` currently divides in the left operand's scale, so `Celsius(20) / Celsius(10)` is 2. Change it to always use the Kelvin scale." It was listed under "decisions needed".
  - The Task 3 commit here, `68b23d1` (2026-10-06), records: "The Temperature / Temperature change to Kelvin is deliberately not made." No reason is written down in this repo. The session doing that work recommended skipping it and it was skipped.
- **Why skipping it was the wrong call — a real example.** mapemounde's `Physics` had been rewritten the day before (`d1b483c`, 2026-10-05) to use typed operators, on the stated expectation that this change was coming:
  ```scala
  // before: correct for any scale
  Meters(pow(temp.toKelvinScale, 2) * radius.toMeters * sqrt(1 - albedo) / (pow(blackbody.toKelvinScale, 2) * 2))
  // after: reads better, and is what the typed API invites
  (radius * (pow(temp / blackbody, 2) * sqrt(1 - albedo) / 2)).in(Meters)
  ```
  With a star at `Celsius(5498.85)` (5772 K) and a target of `Kelvin(278)`, the ratio is 5498.85 / 4.85 = 1134 instead of 20.76, so the returned distance is about 2,980 times too large. Nothing fails or warns. A code review caught it on 2026-10-07, and probing the neighbours found the same fault in two functions that predate the rewrite:
  - `blackbodyTemperatureAt(temp, radius)`, which did `temp * sqrt(...)`
  - `starLuminosity`, which did `temp / solTemperature`

  `starRadius` (`solTemperature / temp`) was right only because its left operand is a Kelvin constant. All three were fixed in mapemounde by converting with `toKelvinScale` by hand, which is exactly the raw-double code the typed operators were meant to replace. Every other consumer of the fork is exposed in the same way.
- **Fix (decision needed):**
  1. Override `divide(that: Temperature)` in `Temperature` to return `this.toKelvinScale / that.toKelvinScale`. This is the change the plan described. It alters results only for a non-Kelvin left operand, where the current result is not physically meaningful.
  2. Decide separately about `times(Double)` and `divide(Double)`. Making `Celsius(10) * 2` return 566.3 K expressed in Celsius is correct for absolute temperatures but wrong for anyone using a `Temperature` as a difference in degrees (`Celsius(5) * 2` meaning a 10-degree step), and `plus` / `minus` already treat the right operand as degrees. Options: leave as is and document it, or scale on the Kelvin scale.
  3. Whatever is chosen, state it in the `Temperature` scaladoc, which today documents the scale-versus-degrees rule for `plus`, `minus`, `to` and `in` but says nothing about multiplication or division.
- **Tests:** `Celsius(20) / Celsius(10)` ≈ 293.15 / 283.15; the four mixed-scale forms above agree; Kelvin ratios unchanged; a ScalaCheck property that `a / b` is the same for `a` and `b` expressed in any scale. Update and list any existing spec that asserts the current behaviour.
- **Caveats:** behaviour change, so it needs a version bump consistent with `early-semver`. `%` and `/%` on `Temperature` (`Quantity.scala:90`-`119`) have the same shape and were not examined.

## Part 1 appendix: removals checked and found safe

- **`scalajs-stubs`:** nothing in the sources references `scalajs`.
- **JS `Test / excludeFilter`:** no `*Serializer*` files exist.
- **`GITHUB_TOKEN` in `scala.yml`:** the build declares no GitHub Packages resolver.
- **Deleted `QuantityBounds` contains/includes test:** the `containsPoint` and `includesPoint` tests cover the same cases.
- **`SolarLuminosities(1)`:** its conversion factor is 3.828e26 W, the same value the test used before.
