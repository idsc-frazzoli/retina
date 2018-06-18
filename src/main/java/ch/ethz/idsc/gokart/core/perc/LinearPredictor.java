// code by vc
package ch.ethz.idsc.gokart.core.perc;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.red.Mean;

public class LinearPredictor {
  private Tensor nextMean = Tensors.empty();
  private Tensor nextHull = Tensors.empty();
  private Tensor nextMean1 = Tensors.empty();// TODO: prediction 2 time steps in the future
  private Tensor nextHull1 = Tensors.empty(); // TODO: same as above

  public LinearPredictor(ClusterCollection collection, double step) {
    for (ClusterDeque clusterDeque : collection.getCollection()) {
      Tensor nonEmptyMeans = clusterDeque.getNonEmptyMeans();
      Tensor hull = clusterDeque.getLast().hull();
      if (Tensors.nonEmpty(nonEmptyMeans)) {
        Tensor predictNextMean = predictNextMean(nonEmptyMeans, step);
        nextMean.append(predictNextMean);
        if (Tensors.nonEmpty(hull))
          nextHull.append(predictNextHull(hull));
      }
    }
  }

  // linear prediction: linear regression in closed form, from the last mean take a step of length st.
  private Tensor predictNextMean(Tensor nonEmptyMeans, double st) {
    Tensor x = Tensor.of(nonEmptyMeans.stream().map(tensor -> tensor.Get(0)));
    Tensor y = Tensor.of(nonEmptyMeans.stream().map(tensor -> tensor.Get(1)));
    if (Tensors.nonEmpty(x)) {
      Tensor b = Tensors.of((x.dot(y)), (x.dot(x))); // TODO
      double beta = b.Get(0).number().doubleValue() / b.Get(1).number().doubleValue();
      double nextX = Last.of(nonEmptyMeans).Get(0).number().doubleValue() + st * Math.cos(beta);
      double nextY = Last.of(nonEmptyMeans).Get(1).number().doubleValue() + st * Math.sin(beta);
      return Tensors.vectorDouble(nextX, nextY);
    }
    return Tensors.empty();
  }

  // assign last hull shape to the new mean
  private Tensor predictNextHull(Tensor hull) {
    if (Tensors.nonEmpty(hull)) {
      Tensor mean = Mean.of(hull);
      Tensor next = Tensors.empty();
      for (Tensor x : hull) {
        Tensor y = x.subtract(mean);
        next.append(Last.of(nextMean).add(y));
      }
      return next;
    }
    return null;
  }

  public Tensor getMeanPredictions() {
    return nextMean.unmodifiable();
  }

  public Tensor getHullPredictions() {
    return nextHull.unmodifiable();
  }
}
