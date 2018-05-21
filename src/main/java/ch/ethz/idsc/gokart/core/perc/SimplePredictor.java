package ch.ethz.idsc.gokart.core.perc;

import ch.ethz.idsc.tensor.Tensor;

public class SimplePredictor {

  public static Tensor getMeanPrediction(ClusterDeque cd) {
    Tensor nonEmptyMeans = cd.getNonEmptyMeans();
    return nonEmptyMeans.get(nonEmptyMeans.length() - 1);
  }

  public static Tensor getHullPrediction(ClusterDeque cd) {
    return cd.getLast().hull();
  }
}
