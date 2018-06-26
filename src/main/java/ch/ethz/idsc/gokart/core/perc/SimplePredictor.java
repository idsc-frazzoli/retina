// code by vc
package ch.ethz.idsc.gokart.core.perc;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Last;

public class SimplePredictor {
  private Tensor nextMeans = Tensors.empty();
  private Tensor nextHulls = Tensors.empty();

  /** @param clusterCollection
   * @return
   * @throws Exception if there are no non-empty means in given cd */
  public SimplePredictor(ClusterCollection collection) {
    for (ClusterDeque x : collection.getCollection()) {
      if (Tensors.nonEmpty(x.getNonEmptyMeans())) {
        Tensor predictedMean = getMeanPrediction(x);
        Tensor predictedHull = getHullPrediction(x);
        nextMeans.append(predictedMean);
        if (Tensors.nonEmpty(predictedHull))
          nextHulls.append(predictedHull);
      }
    }
  }

  private static Tensor getMeanPrediction(ClusterDeque clusterDeque) {
    Tensor nonEmptyMeans = clusterDeque.getNonEmptyMeans();
    return Last.of(nonEmptyMeans);
  }

  private static Tensor getHullPrediction(ClusterDeque clusterDeque) {
    return clusterDeque.getLast().hull();
  }

  public Tensor getMeanPredictions() {
    return nextMeans.unmodifiable();
  }

  public Tensor getHullPredictions() {
    return nextHulls.unmodifiable();
  }
}
