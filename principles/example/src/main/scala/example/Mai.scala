package example

/**
 * @author Grankin Maxim (maximgran@gmail.com) at 18:56 25.10.2020  
 */
object Mai extends App{
  def abs(x:Double) = if (x < 0) -x else x

  def isGoodEnough(guess: Double, x: Double): Boolean = abs(x / guess) < 1e-9

  def improve(guess: Double, x: Double) = (guess + x / guess) / 2

  def sqrtIter(guess: Double, x: Double): Double =
    if (isGoodEnough(guess, x)) guess
    else sqrtIter(improve(guess, x), x)

  def sqrt(x: Double) = sqrtIter(1.0, x)

  println(sqrt(4.0))

}
