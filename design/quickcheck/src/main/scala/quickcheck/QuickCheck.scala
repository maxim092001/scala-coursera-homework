package quickcheck

import org.scalacheck.Arbitrary._
import org.scalacheck.Gen._
import org.scalacheck.Prop._
import org.scalacheck._

import scala.math._

abstract class QuickCheckHeap extends Properties("Heap") with IntHeap {

  lazy val genHeap: Gen[H] = oneOf(const(empty),
    for {
      x <- arbitrary[Int]
      h <- oneOf(const(empty), genHeap)
    } yield insert(x, h)
  )

  implicit lazy val arbHeap: Arbitrary[H] = Arbitrary(genHeap)

  property("gen1") = forAll { (h: H) =>
    val m = if (isEmpty(h)) 0 else findMin(h)
    findMin(insert(m, h)) == m
  }


  property("min1") = forAll { a: Int =>
    val h = insert(a, empty)
    findMin(h) == a
  }


  property("min2") = forAll { (a: Int, b: Int) =>
    val h = insert(a, empty)
    findMin(insert(b, h)) == min(a, b)
  }

  property("delete-min") = forAll { a: Int =>
    isEmpty(deleteMin(insert(a, empty)))
  }

  property("melding") = forAll { (h1: H, h2: H) => {
    val min1 = findMin(h1)
    val min2 = findMin(h2)
    val h = meld(h1, h2)
    findMin(h) == min1 || findMin(h) == min2
  }
  }

  property("empty-melding") = forAll {h: H => {
    val min = findMin(h)
    val min2 = findMin(meld(h, empty))
    min == min2
  }}

}
