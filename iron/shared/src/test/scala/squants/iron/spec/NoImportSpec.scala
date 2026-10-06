package squants.iron.spec

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

// Deliberately does not import squants.iron.given: the snippets below carry their own imports.
class NoImportSpec extends AnyFlatSpec with Matchers {

  behavior of "Iron constraints on quantities"

  it should "not be available until squants.iron.given is imported" in {
    assertDoesNotCompile("""
      import io.github.iltotore.iron.*
      import io.github.iltotore.iron.constraint.numeric.*
      squants.mass.Kilograms(5).refineEither[Positive]
    """)
  }

  it should "be available once squants.iron.given is imported" in {
    assertCompiles("""
      import io.github.iltotore.iron.*
      import io.github.iltotore.iron.constraint.numeric.*
      import squants.iron.given
      squants.mass.Kilograms(5).refineEither[Positive]
    """)
  }

  it should "still work for plain Doubles without the import" in {
    assertCompiles("""
      import io.github.iltotore.iron.*
      import io.github.iltotore.iron.constraint.numeric.*
      5.0.refineEither[Positive]
    """)
  }
}
