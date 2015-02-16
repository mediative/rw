object RW {
  import scalaz._, Scalaz._, effect._, IO._
  import scalaz.concurrent.Task

  import java.nio.file._
  import java.nio.charset._

  abstract class Sym[repr[_]: Monad, path, content] {
    def lift[T]: T => repr[T]
    def read: repr[path] => repr[content]
    def write: repr[path] => repr[content] => repr[Unit]
    def delete: repr[path] => repr[Boolean]
  }

  implicit object EvalSym extends Sym[IO, Path, String] {
    def lift[T] = v => IO(v)
    def read = loc => {
      loc.map{ p =>
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
}