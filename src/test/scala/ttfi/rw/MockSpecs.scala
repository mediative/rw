package ttfi
package rw

import org.specs2.{ Specification, ScalaCheck }

class MockSpecs extends Specification with ScalaCheck {
  def is = s2"""
  mock $mocking
  mock monad laws $mockMonadLaws
"""
  import rw.mock._
  import rw.mock.MockAdt._
  import rw.Usage._

  import scala.collection.immutable.Queue

  def mocking = {
    val p1 = MockPath("p1"); val p2 = MockPath("p2"); val p1c = MockContent("foo")

    val e = e1[Mock, MockPath, MockContent](p1, p2)
    val result = e((Queue.empty[IOOps], Map(p1 -> p1c)))

    val expected =
      ((
        Queue(
          Read(p1, p1c),
          Delete(p2, executed = false),
          Write(p2, p1c)
        ),
          Map(
            p1 -> p1c,
            p2 -> p1c
          )
      ), ())
    result ==== expected
  }

  def mockMonadLaws = {
    // see * https://github.com/rickynils/scalacheck/wiki/User-Guide#concepts
    // on how to implement Arbitrary for case classes

    // see * http://eed3si9n.com/learning-scalaz/Monad+laws.html
    //     * https://github.com/scalaz/scalaz/blob/v7.1.1/scalacheck-binding/src/main/scala/scalaz/scalacheck/ScalazProperties.scala#L202
    //     * https://github.com/scalaz/scalaz/blob/v7.1.1/scalacheck-binding/src/main/scala/scalaz/scalacheck/ScalazArbitrary.scala#L256
    //     * https://github.com/scalaz/scalaz/blob/v7.1.1/scalacheck-binding/src/main/scala/scalaz/scalacheck/ScalaCheckBinding.scala#L11
    // on how to use scalaz defined laws for properties checking

    // import scalacheck._
    // import ScalazProperties._

    // import org.scalacheck._
    // import Gen._
    // import Arbitrary.arbitrary

    // monad.laws[Mock]
    pending
  }
}