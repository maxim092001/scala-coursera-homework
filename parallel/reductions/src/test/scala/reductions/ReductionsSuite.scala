package reductions

import java.util.concurrent._
import scala.collection._
import java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory
import org.junit._
import org.junit.Assert.assertEquals

class ReductionsSuite {
  /*****************
   * LINE OF SIGHT *
   *****************/

  import LineOfSight._
  @Test def `lineOfSight should correctly handle an array of size 4`: Unit = {
    val output = new Array[Float](4)
    lineOfSight(Array[Float](0f, 1f, 8f, 9f), output)
    assertEquals(List(0f, 1f, 4f, 4f), output.toList)
  }




  /*******************************
   * PARALLEL COUNT CHANGE SUITE *
   *******************************/

  import ParallelCountChange._

  @Test def `countChange should return 0 for money < 0`: Unit = {
    def check(money: Int, coins: List[Int]) =
      assert(countChange(money, coins) == 0,
        s"countChang($money, _) should be 0")

    check(-1, List())
    check(-1, List(1, 2, 3))
    check(-Int.MinValue, List())
    check(-Int.MinValue, List(1, 2, 3))
  }

  @Test def `countChange should return 1 when money == 0`: Unit = {
    def check(coins: List[Int]) =
      assert(countChange(0, coins) == 1,
        s"countChang(0, _) should be 1")

    check(List())
    check(List(1, 2, 3))
    check(List.range(1, 100))
  }

  @Test def `countChange should return 0 for money > 0 and coins = List()`: Unit = {
    def check(money: Int) =
      assert(countChange(money, List()) == 0,
        s"countChang($money, List()) should be 0")

    check(1)
    check(Int.MaxValue)
  }

  @Test def `countChange should work when there is only one coin`: Unit = {
    def check(money: Int, coins: List[Int], expected: Int) =
      assert(countChange(money, coins) == expected,
        s"countChange($money, $coins) should be $expected")

    check(1, List(1), 1)
    check(2, List(1), 1)
    check(1, List(2), 0)
    check(Int.MaxValue, List(Int.MaxValue), 1)
    check(Int.MaxValue - 1, List(Int.MaxValue), 0)
  }

  @Test def `countChange should work for multi-coins`: Unit = {
    def check(money: Int, coins: List[Int], expected: Int) =
      assert(countChange(money, coins) == expected,
        s"countChange($money, $coins) should be $expected")

    check(50, List(1, 2, 5, 10), 341)
    check(250, List(1, 2, 5, 10, 20, 50), 177863)
  }


  /**********************************
   * PARALLEL PARENTHESES BALANCING *
   **********************************/

  import ParallelParenthesesBalancing._

  @Test def `balance should work for empty string`: Unit = {
    def check(input: String, expected: Boolean) =
      assert(balance(input.toArray) == expected,
        s"balance($input) should be $expected")

    check("", true)
  }

  @Test def `balance should work for string of length 1`: Unit = {
    def check(input: String, expected: Boolean) =
      assert(balance(input.toArray) == expected,
        s"balance($input) should be $expected")

    check("(", false)
    check(")", false)
    check(".", true)
  }

  @Test def `balance should work for string of length 2`: Unit = {
    def check(input: String, expected: Boolean) =
      assert(balance(input.toArray) == expected,
        s"balance($input) should be $expected")

    check("()", true)
    check(")(", false)
    check("((", false)
    check("))", false)
    check(".)", false)
    check(".(", false)
    check("(.", false)
    check(").", false)
  }


  @Rule def individualTestTimeout = new org.junit.rules.Timeout(10 * 1000)
}

