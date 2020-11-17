package recfun

import scala.annotation.tailrec

object RecFun extends RecFunInterface {

  def main(args: Array[String]): Unit = {
    println("Pascal's Triangle")
    for (row <- 0 to 10) {
      for (col <- 0 to row)
        print(s"${pascal(col, row)} ")
      println()
    }
  }

  /**
   * Exercise 1
   */
  def pascal(c: Int, r: Int): Int = {
    if (c == 0 || c == r) 1 else pascal(c - 1, r - 1) + pascal(c, r - 1)
  }

  /**
   * Exercise 2
   */
  def balance(chars: List[Char]): Boolean = {
    @tailrec
    def findCBS(position: Int, stack: List[Char]): Boolean = {
      if (position == chars.length) stack.isEmpty
      else {
        val currentChar = chars(position)

        if (currentChar == '(') {
          findCBS(position + 1, currentChar :: stack)
        } else if (currentChar == ')') {
          stack match {
            case '(' :: tail => findCBS(position + 1, tail)
            case _ => false
          }
        } else {
          findCBS(position + 1, stack)
        }
      }
    }

    chars match {
      case Nil => false
      case _ => findCBS(0, List())
    }
  }

  /**
   * Exercise 3
   */
  def countChange(money: Int, coins: List[Int]): Int = {
    if (money == 0) 1
    else if (money < 0) 0
    else if (coins.isEmpty) 0
    else countChange(money - coins.head, coins) + countChange(money, coins.tail)
  }
}
