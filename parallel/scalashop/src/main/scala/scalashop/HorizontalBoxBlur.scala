package scalashop

import org.scalameter._

import scala.math.{max, min}

object HorizontalBoxBlurRunner {

  val standardConfig = config(
    Key.exec.minWarmupRuns -> 5,
    Key.exec.maxWarmupRuns -> 10,
    Key.exec.benchRuns -> 10,
    Key.verbose -> true
  ) withWarmer (new Warmer.Default)

  def main(args: Array[String]): Unit = {
    val radius = 3
    val width = 1920
    val height = 1080
    val src = new Img(width, height)
    val dst = new Img(width, height)
    val seqtime = standardConfig measure {
      HorizontalBoxBlur.blur(src, dst, 0, height, radius)
    }
    println(s"sequential blur time: $seqtime")

    val numTasks = 32
    val partime = standardConfig measure {
      HorizontalBoxBlur.parBlur(src, dst, numTasks, radius)
    }
    println(s"fork/join blur time: $partime")
    println(s"speedup: ${seqtime.value / partime.value}")
  }
}

/** A simple, trivially parallelizable computation. */
object HorizontalBoxBlur extends HorizontalBoxBlurInterface {

  /** Blurs the rows of the source image `src` into the destination image `dst`,
   * starting with `from` and ending with `end` (non-inclusive).
   *
   * Within each row, `blur` traverses the pixels by going from left to right.
   */
  def blur(src: Img, dst: Img, from: Int, end: Int, radius: Int): Unit = {
    for (
      row <- 0 until src.width;
      col <- max(0, from) until min(src.height, end)
    ) yield {
      dst.update(row, col, boxBlurKernel(src, row, col, radius))
    }
  }

  /** Blurs the rows of the source image in parallel using `numTasks` tasks.
   *
   * Parallelization is done by stripping the source image `src` into
   * `numTasks` separate strips, where each strip is composed of some number of
   * rows.
   */
  def parBlur(src: Img, dst: Img, numTasks: Int, radius: Int): Unit = {
    val horizontalStrips: Seq[RGBA] = (0 to src.height).by(max(1, src.height / numTasks))
    horizontalStrips.zip(horizontalStrips.tail).map(p => task[Unit]{ blur(src, dst, p._1, p._2, radius) }).foreach(_.join())
  }

}
