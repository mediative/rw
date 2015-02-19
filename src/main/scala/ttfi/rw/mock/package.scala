package ttfi.rw

package object mock extends MockInstances {

  import MockAdt._

  import scalaz.State
  import scala.collection.immutable.Queue

  type MockFS = Map[MockPath, MockContent]
  type Mock[A] = State[(Queue[IOOps], MockFS), A]
}