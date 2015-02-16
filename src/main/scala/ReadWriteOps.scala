object RW {
  import scalaz._, Scalaz._, effect._, IO._
  import scalaz.concurrent.Task

  import java.nio.file._
  import java.nio.charset._

  import scala.collection.immutable.Queue

  // TODO: find a way to handle failure, like file not found, etc
  abstract class Sym[repr[_], path, content](implicit m: Monad[repr]) {
    implicit def lift[T]: T => repr[T] = v => m.point(v)
    def read: repr[path] => repr[content]
    def write: repr[path] => repr[content] => repr[Unit]
    def delete: repr[path] => repr[Boolean]
  }

  implicit object EvalSym extends Sym[IO, Path, String] {
    def read = loc => {
      loc.map { p =>
        import scala.collection.JavaConverters._
        Files.readAllLines(p).asScala.mkString(System.lineSeparator)
      }
    }

    def write = loc => content =>
      for {
        l <- loc
        c <- content
      } yield {
        Files.write(l, c.getBytes(Charset.forName("UTF-8")))
        ()
      }

    def delete = loc =>
      loc.map(l => Files.deleteIfExists(l))
  }

  case class MockPath(p: String)
  case class MockContent(p: String)

  sealed trait IOOps
  case class Read(path: MockPath, content: MockContent) extends IOOps
  case class Write(path: MockPath, content: MockContent) extends IOOps
  case class Delete(path: MockPath, executed: Boolean) extends IOOps

  type MockFS = Map[MockPath, MockContent]
  type Mock[A] = State[(Queue[IOOps], MockFS), A]

  implicit object MockSym extends Sym[Mock, MockPath, MockContent] {
    def delete: Mock[MockPath] => Mock[Boolean] = _.mapK {
      case ((_, fs), path) =>
        val deleted = fs.contains(path)
        ((Queue(Delete(path, deleted)), fs - path), deleted)
    }

    def read: Mock[MockPath] => Mock[MockContent] = _.mapK {
      case ((_, fs), path) =>
        val content = fs.get(path) | MockContent("not found") // FIXME: not found
        ((Queue(Read(path, content)), fs), content)
    }

    def write: Mock[MockPath] => Mock[MockContent] => Mock[Unit] = mp => mc =>
      mp.flatMap(path => State { s =>
        val ((_, fs), content) = mc(s)
        ((Queue(Write(path, content)), fs + (path -> content)), ())
      })
  }

  // FIXME: check monad's laws with scalacheck
  implicit object MockMonad extends Monad[Mock] {
    def point[A](a: => A): Mock[A] = State((_, a))
    override def map[A, B](m: Mock[A])(f: A => B): Mock[B] = State { s =>
      val (s1, a) = m(s)
      (s1, f(a))
    }
    def bind[A, B](m: Mock[A])(f: A => Mock[B]): Mock[B] = join2(m.map(f))
    def join2[A](m: Mock[Mock[A]]): Mock[A] = State { s =>
      val (s1, m1) = m(s)
      val (ops1, fs1) = s1
      val ((ops2, fs2), a) = m1(s1)
      ((ops1 ++ ops2, fs2), a)
    }
  }
}