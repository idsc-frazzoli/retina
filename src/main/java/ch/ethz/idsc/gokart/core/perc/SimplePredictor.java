// code by vc
package ch.ethz.idsc.gokart.core.perc;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Last;

public class SimplePredictor {
  /** @param clusterDeque
   * @return
   * @throws Exception if there are no non-empty means in given cd */
  public static Tensor getMeanPrediction(ClusterDeque clusterDeque) {
    Tensor nonEmptyMeans = clusterDeque.getNonEmptyMeans();
    return Last.of(nonEmptyMeans);
    // return nonEmptyMeans.get(nonEmptyMeans.length() - 1);
  }

  public static Tensor getHullPrediction(ClusterDeque clusterDeque) {
    return clusterDeque.getLast().hull();
  }
}
