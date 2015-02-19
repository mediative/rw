package ttfi.rw
package mock

trait MockMonadInstances {
  import scalaz.{ State, Monad }

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