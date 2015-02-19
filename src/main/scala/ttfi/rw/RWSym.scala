package ttfi.rw

import scalaz.Monad

// TODO: find a way to handle failure, like file not found, etc
abstract class RWSym[repr[_], path, content](implicit m: Monad[repr]) {
  implicit def lift[T]: T => repr[T] = v => m.point(v)
  def read: repr[path] => repr[content]
  def write: repr[path] => repr[content] => repr[Unit]
  def delete: repr[path] => repr[Boolean]
}