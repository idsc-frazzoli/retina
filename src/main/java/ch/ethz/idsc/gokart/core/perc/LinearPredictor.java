// code by vc
package ch.ethz.idsc.gokart.core.perc;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Flatten;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.mat.Inverse;
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
      Tensor predictNextMean = predictNextMean(nonEmptyMeans, step);
      if (Tensors.nonEmpty(predictNextMean))
        nextMean.append(predictNextMean);
      if (Tensors.nonEmpty(hull) && Tensors.nonEmpty(nextMean))
        nextHull.append(predictNextHull(hull));
    }
  }

  // linear prediction: linear regression in closed form, from the last mean take a step of length st.
  private Tensor predictNextMean(Tensor nonEmptyMeans, double st) {
    Tensor x = Tensor.of(nonEmptyMeans.stream().map(tensor -> Tensors.of(tensor.Get(0), RealScalar.of(1))));// homogeneous coordinates
    Tensor y = Tensor.of(nonEmptyMeans.stream().map(tensor -> tensor.Get(1)));
    if (Tensors.nonEmpty(x)) {
      if (x.length() > 1) { // fit a line if more than one point
        Tensor beta = Inverse.of(Transpose.of(x).dot(x)).dot(Transpose.of(x).dot(y));
        double b = beta.Get(0).number().doubleValue();
        double nextX = Last.of(nonEmptyMeans).Get(0).number().doubleValue() + st * Math.cos(b);
        double nextY = Last.of(nonEmptyMeans).Get(1).number().doubleValue() + st * Math.sin(b);
        return Tensors.vectorDouble(nextX, nextY);
      } else
        return Flatten.of(nonEmptyMeans); // if only one point assume it is not going to move
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
    return Tensors.empty();
  }

  public Tensor getMeanPredictions() {
    return nextMean.unmodifiable();
  }

  public Tensor getHullPredictions() {
    return nextHull.unmodifiable();
  }
}
