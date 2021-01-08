package kmeans

import scala.collection.{Map, Seq}
import scala.collection.parallel.{ParMap, ParSeq}

/**
 * The interface used by the grading infrastructure. Do not change signatures
 * or your submission will fail with a NoSuchMethodError.
 */
trait KMeansInterface {
  def classify(points: Seq[Point], means: Seq[Point]): Map[Point, Seq[Point]]
  def classify(points: ParSeq[Point], means: ParSeq[Point]): ParMap[Point, ParSeq[Point]]
  def update(classified: Map[Point, Seq[Point]], oldMeans: Seq[Point]): Seq[Point]
  def update(classified: ParMap[Point, ParSeq[Point]], oldMeans: ParSeq[Point]): ParSeq[Point]
  def converged(eta: Double, oldMeans: Seq[Point], newMeans: Seq[Point]): Boolean
  def converged(eta: Double, oldMeans: ParSeq[Point], newMeans: ParSeq[Point]): Boolean
  def kMeans(points: Seq[Point], means: Seq[Point], eta: Double): Seq[Point]
  def kMeans(points: ParSeq[Point], means: ParSeq[Point], eta: Double): ParSeq[Point]
}
