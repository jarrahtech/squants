/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2013-2015, Gary Keorkunian                                       **
**                                                                      **
\*                                                                      */

package squants

import java.nio.file.{ FileSystems, Files, Path, Paths }

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.jdk.CollectionConverters._
import scala.util.Using

/**
 * JVM only: the dimensions are found by scanning the compiled main classes, so a new dimension is covered
 * without being added to a list here.
 */
class DimensionSymbolSpec extends AnyFlatSpec with Matchers {

  // Names of the compiled objects under a directory of classes
  private def objectNames(root: Path): List[String] = Using.resource(Files.walk(root).nn) { paths =>
    paths.iterator.nn.asScala.map(root.relativize(_).toString)
      .filter(_.endsWith("$.class"))
      .map(_.stripSuffix(".class").replace(root.getFileSystem.nn.getSeparator.nn, "."))
      .toList
  }

  private val dimensions: Seq[Dimension[?]] = {
    val dimensionClass = classOf[Dimension[?]]
    // sbt puts the main classes on the test classpath as a directory or as a jar
    val location = Paths.get(dimensionClass.getProtectionDomain.nn.getCodeSource.nn.getLocation.nn.toURI).nn
    val names =
      if (Files.isDirectory(location)) objectNames(location)
      else Using.resource(FileSystems.newFileSystem(location).nn)(jar => objectNames(jar.getPath("/").nn))
    names
      .map(Class.forName(_, false, dimensionClass.getClassLoader))
      .filter(dimensionClass.isAssignableFrom)
      .map(_.getField("MODULE$").nn.get(null).asInstanceOf[Dimension[?]])
      .filter(_ != market.Money) // Money has no fixed units, they come from a MoneyContext
  }

  "The classpath scan" should "find the dimensions" in {
    dimensions.size should be >= 70
    dimensions should contain allOf (energy.Power, space.Length, thermal.Temperature)
  }

  "Dimensions" should "not share a symbol between two of their units" in {
    for (dimension <- dimensions) withClue(dimension.name) {
      val symbols = dimension.units.toSeq.map(_.symbol)
      symbols.diff(symbols.distinct) should be(empty)
    }
  }
}
