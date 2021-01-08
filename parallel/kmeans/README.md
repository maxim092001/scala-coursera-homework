# K-Means

You can find the starting files for this assignment [here](https://moocs.scala-lang.org/~dockermoocs/handouts-coursera-2.13/kmeans.zip).

In this assignment, you will implement the K-means algorithm for cluster detection, which is used to partition n vectors into k clusters. Here, vectors are separated into clusters based on their mutual similarity -- vectors that are closer to each other in space are more likely to end up in the same cluster, and the distant vectors are likely to be in different clusters. K-means has many applications: it is used in data mining, image filtering and signal processing.

Here is a simple example -- let's say that we have a set of vectors in 2D space, as shown in the following figure:


![First](https://github.com/maxim092001/Scala-Coursera-Homework/blob/master/parallel/kmeans/src/main/resources/kmeans/1.png)


As a human, you can visually distinguish the three clusters of points in the image:


![Second](https://github.com/maxim092001/Scala-Coursera-Homework/blob/master/parallel/kmeans/src/main/resources/kmeans/2.png)


When the number of clusters, dimensions and vectors grows, it becomes difficult and even impossible to manually determine the clusters. K-means is a simple algorithm that takes a set of vectors (called points) and outputs as set of clusters as follows:

Pick k points called means. This is called initialization.
    Associate each input point with the mean that is closest to it. We obtain k clusters of points, and we refer to this process as classifying the points.
    Update each mean to have the average value of the corresponding cluster.
    If the k means have significantly changed, go back to step 2. If they did not, we say that the algorithm converged.
    The k means represent different clusters -- every point is in the cluster corresponding to the closest mean.

Above, two steps need additional discussion. First, how do we pick the initial k means? The initialization step can be done in many different ways -- we will just randomly pick some of the input vectors. Second, how do we know that the algorithm converged? We will check that, for each mean, the square distance between the old value of the mean and the new value of the mean is less than or equal to some value eta.

For a better illustration, here are a few steps of the K-means algorithm. Initially, we pick a random set of means, shown with "X" in the figure:


![Third](https://github.com/maxim092001/Scala-Coursera-Homework/blob/master/parallel/kmeans/src/main/resources/kmeans/3.png)


Then, we classify the points according to the closest mean ("X"). The means divide the space into regions, where each point is closer to the corresponding mean than any other mean -- in the figure, the dotted line depicts the borders of different regions:


![Fourth](https://github.com/maxim092001/Scala-Coursera-Homework/blob/master/parallel/kmeans/src/main/resources/kmeans/4.png)


All the points in the same region form one cluster. After having classified the points, we can update the mean values to the average of all the points in the cluster:


![Fifth](https://github.com/maxim092001/Scala-Coursera-Homework/blob/master/parallel/kmeans/src/main/resources/kmeans/5.png)


Each of the means was significantly updated. This is a good indication that the algorithm did not yet converge, so we repeat the steps again -- we first classify all the points:


![Sixth](https://github.com/maxim092001/Scala-Coursera-Homework/blob/master/parallel/kmeans/src/main/resources/kmeans/6.png)


And then we update the means again:


![Seventh](https://github.com/maxim092001/Scala-Coursera-Homework/blob/master/parallel/kmeans/src/main/resources/kmeans/7.png)


One of the means did not change at all in the last step. Still, other means have changed so we continue this process until the change in the position of each point drops below the eta value.

At each iteration of K-means, we can associate multiple points to clusters, and compute the average of the k clusters, in parallel. Note that the association of a point to its cluster is independent of the other points in the input, and similarly, the computation of the average of a cluster is independent of the other clusters. Once all parallel tasks of the current iteration complete, the algorithm can proceed to the next iteration.

K-means is an example of a bulk synchronous parallel algorithm (BSP). BSP algorithms are composed from a sequence of supersteps, each of which contains:

* parallel computation, in which processes independently perform local computations and produce some values
* communication, in which processes exchange data
* barrier synchronisation, during which processes wait until every process finishes

Data-parallel programming models are typically a good fit for BSP algorithms, as each bulk synchronous phase can correspond to some number of data-parallel operations.

### Classifying the points

In the first part of this assignment, you will classify the input points according to the square distance to the means. Input points are described with the following Point data-type:

```scala
class Point(val x: Double, val y: Double, val z: Double)
```

You will start by implementing the classify method:

```scala
def classify(points: Seq[Point], means: Seq[Point]): Map[Point, Seq[Point]]
def classify(points: ParSeq[Point], means: ParSeq[Point]): ParMap[Point, ParSeq[Point]]
```

There are two overloads of this method. The first one works with sequential collections, whereas the second one works with parallel collections.

These two methods take a sequence of points and a sequence of means, and return a map collection, which maps each mean to the sequence of points in the corresponding cluster.

Note that the generic collection types introduced in the lectures (e.g., GenSeq, GenMap) do not exist anymore in the last version of the Scala standard library. Consequently, it is not anymore possible to write algorithms that are agnostic about parallelism. However, both parallel and sequential collections have a similar API. This means that once you have implemented one overload of classify, the other one can be implemented by copy-pasting the first implementation.

Hint: Use groupBy and the findClosest method, which is already defined for you. After that, make sure that all the means are in the resulting map, even if their sequences are empty.

### Updating the means

In the second part of this assignment, you will update the means corresponding to different clusters.

Implement the method update, which takes the map of classified points produced in the previous step, and the sequence of previous means. The method returns the new sequence of means:


```scala
def update(classified: ParMap[Point, ParSeq[Point]], oldMeans: ParSeq[Point]): ParSeq[Point]
```

Once more, there are two overloads of the method, one working with sequential collections, and the other working with parallel collections.

Take care to preserve order in the resulting sequence -- the mean i in the resulting sequence must correspond to the mean i from oldMeans.

Hint: Make sure you use the findAverage method that is predefined for you.

### Detecting convergence

Finally, you will implement convergence detection. The convergence detection method takes a sequence of old means and the sequence of updated means, and returns a boolean indicating if the algorithm converged or not. Given an eta parameter, oldMeans and newMeans, it returns true if the algorithm converged, and false otherwise:


```scala
def converged(eta: Double, oldMeans: ParSeq[Point], newMeans: ParSeq[Point])
```

The algorithm converged iff the square distance between the old and the new mean is less than or equal to eta, for all means.

Note: the means in the two lists are ordered -- the mean at i in oldMeans is the previous value of the mean at i in newMeans.

Implement converged!

### Running the algorithm

We now have everything we need to run the K-means algorithm. We only need to combine the previously defined methods in the right way.

The tail-recursive kMeans method takes a sequence of points points, previously computed sequence of means means, and the eta value:

```scala
@tailrec final def kMeans(points: Seq[Point], means: Seq[Point], eta: Double): Seq[Point]
@tailrec final def kMeans(points: ParSeq[Point], means: ParSeq[Point], eta: Double): ParSeq[Point]
```

The kMeans method should return the sequence of means, each corresponding to a specific cluster.

Hint: kMeans implements the steps 2-4 from the K-means pseudocode.

Run the algorithm and report the speedup:

```
> runMain kmeans.KMeansRunner
```

