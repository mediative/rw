package ttfi.rw.mock

object MockAdt {
  case class MockPath(p: String)
  case class MockContent(p: String)

  sealed trait IOOps
  case class Read(path: MockPath, content: MockContent) extends IOOps
  case class Write(path: MockPath, content: MockContent) extends IOOps
  case class Delete(path: MockPath, executed: Boolean) extends IOOps
}