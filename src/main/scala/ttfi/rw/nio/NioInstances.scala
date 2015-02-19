package ttfi
package rw
package nio

trait NioInstances {
  import scalaz.effect.IO

  import scala.collection.immutable.Queue

  import java.nio.file._
  import java.nio.charset._

  implicit def evalSym: RWSym[IO, Path, String] =
    new RWSym[IO, Path, String] {
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
}