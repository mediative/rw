import org.specs2.{ Specification, ScalaCheck }

class ReadWriteSpecs extends Specification with ScalaCheck {
  def is = s2"""
  effectfull implementation $effects
  mock implementation $mocking
  mock monad follow monad's laws $mockMonadLaws
"""
  import scalaz._, Scalaz._, effect._, IO._
  import java.nio.file._
  import scala.collection.immutable.Queue

  import RW._

  def e1[repr[_]: Monad, path, content](p1: path, p2: path)(implicit s: Sym[repr, path, content]) = {
    import s._

    for {
      content <- read(p1)
      _ <- delete(p2)
      _ <- write(p2)(identity(content))
    } yield ()
  }

  def effects = {
    def path(a: String) = Paths.get(getClass().getResource("/effect").toURI()).resolve(a)
    def content(p: Path) = Files.readAllLines(p)

    val p1 = path("p1"); val p2 = path("p2")

    e1[IO, Path, String](p1, p2).unsafePerformIO

    content(p1) ==== content(p2)
  }

  def mocking = {
    val p1 = MockPath("p1"); val p2 = MockPath("p2"); val p1c = MockContent("foo")

    val e = e1[Mock, MockPath, MockContent](p1, p2)
    val result = e((Queue.empty[IOOps], Map(p1 -> p1c)))

    val expected =
      ((
        Queue(
          Read(p1, p1c),
          Delete(p2, executed = false),
          Write(p2, p1c)
        ),
          Map(
            p1 -> p1c,
            p2 -> p1c
          )
      ), ())
    result ==== expected
  }

  def mockMonadLaws = {
    // see * https://github.com/rickynils/scalacheck/wiki/User-Guide#concepts
    // on how to implement Arbitrary for case classes

    // see * http://eed3si9n.com/learning-scalaz/Monad+laws.html
    //     * https://github.com/scalaz/scalaz/blob/v7.1.1/scalacheck-binding/src/main/scala/scalaz/scalacheck/ScalazProperties.scala#L202
    //     * https://github.com/scalaz/scalaz/blob/v7.1.1/scalacheck-binding/src/main/scala/scalaz/scalacheck/ScalazArbitrary.scala#L256
    //     * https://github.com/scalaz/scalaz/blob/v7.1.1/scalacheck-binding/src/main/scala/scalaz/scalacheck/ScalaCheckBinding.scala#L11
    // on how to use scalaz defined laws for properties checking

    // import scalacheck._
    // import ScalazProperties._

    // import org.scalacheck._
    // import Gen._
    // import Arbitrary.arbitrary

    // monad.laws[Mock]
    pending
  }
}