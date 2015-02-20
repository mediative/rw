package ttfi.rw
package mock

trait MockInstances {
  import MockAdt._

  import scalaz.{ State, Monad }

  import scalaz.State
  import scalaz.syntax.std.option._
  import scala.collection.immutable.Queue

  implicit val mockRWSym: RWSym[Mock, MockPath, MockContent] =
    new RWSym[Mock, MockPath, MockContent] {
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
  implicit val mockMonad: Monad[Mock] =
    new Monad[Mock] {
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