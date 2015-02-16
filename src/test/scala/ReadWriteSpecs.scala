import TTFI._

import org.specs2._
class ReadWriteSpecs extends Specification { def is = s2"""
	effectfull implementation	$effects
"""

	import scalaz._, Scalaz._, effect._, IO._
	import java.nio.file._

	import RW._

	def e1[repr[_]: Monad, path, content](p1: path, p2: path)(implicit s: Sym[repr, path, content]) = {
    import s._

    for {
      content <- read(lift(p1))
      _ <- delete(lift(p2))
      _ <- write(lift(p2))(lift(identity(content)))
    } yield ()
  }


	def effects = {
		def path(a: String) = Paths.get(getClass().getResource("/effect").toURI()).resolve(a)
		def content(p: Path) = Files.readAllLines(p)

		val p1 = path("p1")
		val p2 = path("p2")

		e1[IO, Path, String](p1, p2).unsafePerformIO
		
		content(p1) ==== content(p2)
	}
}