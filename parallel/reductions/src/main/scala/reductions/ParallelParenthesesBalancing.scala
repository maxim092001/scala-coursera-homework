package reductions

import scala.annotation._
import org.scalameter._
import scala.math.min

object ParallelParenthesesBalancingRunner {

  @volatile var seqResult = false

  @volatile var parResult = false

  val standardConfig = config(
    Key.exec.minWarmupRuns -> 40,
    Key.exec.maxWarmupRuns -> 80,
    Key.exec.benchRuns -> 120,
    Key.verbose -> true
  ) withWarmer (new Warmer.Default)

  def main(args: Array[String]): Unit = {
    val length = 100000000
    val chars = new Array[Char](length)
    val threshold = 10000
    val seqtime = standardConfig measure {
      seqResult = ParallelParenthesesBalancing.balance(chars)
    }
    println(s"sequential result = $seqResult")
    println(s"sequential balancing time: $seqtime")

    val fjtime = standardConfig measure {
      parResult = ParallelParenthesesBalancing.parBalance(chars, threshold)
    }
    println(s"parallel result = $parResult")
    println(s"parallel balancing time: $fjtime")
    println(s"speedup: ${seqtime.value / fjtime.value}")
  }
}

object ParallelParenthesesBalancing extends ParallelParenthesesBalancingInterface {

  /** Returns `true` iff the parentheses in the input `chars` are balanced.
   */
  def balance(chars: Array[Char]): Boolean = {

    @tailrec
    def countBalance(currentIndex: Int, counter: Int): Int = {
      if (chars.length == currentIndex) counter
      else if (counter < 0) -1
      else chars(currentIndex) match {
        case '(' => countBalance(currentIndex + 1, counter + 1)
        case ')' => countBalance(currentIndex + 1, counter - 1)
        case _ => countBalance(currentIndex + 1, counter)
      }
    }

    countBalance(0, 0) == 0
  }

  /** Returns `true` iff the parentheses in the input `chars` are balanced.
   */
  def parBalance(chars: Array[Char], threshold: Int): Boolean = {

    @tailrec
    def traverse(idx: Int, until: Int, arg1: Int, arg2: Int): (Int, Int) = {
      if (idx >= until) (arg1, arg2)
      else chars(idx) match {
        case ')' => traverse(idx + 1, until, arg1 - 1, arg2 + 1)
        case '(' => traverse(idx + 1, until, arg1 + 1, arg2 - 1)
        case _ => traverse(idx + 1, until, arg1, arg2)
      }
    }

    def reduce(from: Int, until: Int): (Int, Int) = {
      if (until - from <= threshold) traverse(from, until, 0, 0)
      else {
        val middle = (until + from) / 2
        val ((left1, left2), (right1, right2)) = parallel(
          reduce(from, middle),
          reduce(middle, until))

        (left1 + right1, left2 + right2)
      }
    }

    reduce(0, chars.length) == (0, 0)
  }

}
