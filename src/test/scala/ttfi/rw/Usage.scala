package ttfi
package rw

object Usage {
  import scalaz.Monad

  def e1[repr[_], path, content](p1: path, p2: path)(implicit s: RWSym[repr, path, content], m: Monad[repr]) = {
    import s._
    import m.monadSyntax._

    for {
      content <- read(p1)
      _ <- delete(p2)
      _ <- write(p2)(identity(content))
    } yield ()
  }
}