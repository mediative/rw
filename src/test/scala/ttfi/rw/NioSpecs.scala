package ttfi
package rw

import org.specs2.Specification

class NioSpecs extends Specification {
  def is = s2"""
  effectfull implementation $effects
"""

  import rw.nio._

  import rw.Usage._

  import scalaz.effect.IO

  import java.nio.file._

  def effects = {
    def path(a: String) = Paths.get(getClass().getResource("/rw").toURI()).resolve(a)
    def content(p: Path) = Files.readAllLines(p)

    val p1 = path("p1"); val p2 = path("p2")

    e1[IO, Path, String](p1, p2).unsafePerformIO

    content(p1) ==== content(p2)
  }
}