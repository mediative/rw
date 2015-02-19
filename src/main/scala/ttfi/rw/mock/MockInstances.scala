package ttfi.rw
package mock

trait MockInstances extends MockMonadInstances {
  import MockAdt._

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
}