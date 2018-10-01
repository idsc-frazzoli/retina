// code by vc
package ch.ethz.idsc.gokart.core.perc;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Flatten;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.lie.AngleVector;
import ch.ethz.idsc.tensor.mat.LeastSquares;
import ch.ethz.idsc.tensor.red.Mean;

public class LinearPredictor {
  private final Tensor nextMeans = Tensors.empty();
  private final Tensor nextHulls = Tensors.empty();
  private final Tensor twiceNextMeans = Tensors.empty(); // prediction 2 time steps in the future
  private final Tensor twiceNextHulls = Tensors.empty();

  public LinearPredictor(ClusterCollection collection) {
    for (ClusterDeque clusterDeque : collection.getCollection()) {
      Tensor nonEmptyMeans = clusterDeque.getNonEmptyMeans();
      Tensor hull = clusterDeque.getLast().hull();
      predictNextMean(nonEmptyMeans);
      if (Tensors.nonEmpty(nextMeans) && Tensors.nonEmpty(twiceNextMeans))
        predictNextHull(hull);
    }
  }

  private void predictNextMean(Tensor nonEmptyMeans) {
    Tensor predictMean = predictMean(nonEmptyMeans, 1);
    if (Tensors.nonEmpty(predictMean)) {
      nextMeans.append(predictMean);
      twiceNextMeans.append(predictMean(nonEmptyMeans, 2));
    }
  }

  // linear prediction: linear regression in closed form, from the last mean take a step of length i*step
  private static Tensor predictMean(Tensor nonEmptyMeans, int i) {
    Tensor x = Tensor.of(nonEmptyMeans.stream().map(tensor -> Tensors.of(tensor.Get(0), RealScalar.of(1)))); // homogeneous coordinates
    Tensor y = nonEmptyMeans.get(Tensor.ALL, 1);
    if (Tensors.nonEmpty(x)) {
      if (1 < x.length()) { // fit a line if more than one point
        Tensor subtract = Tensor.of(x.extract(0, x.length() - 2).subtract(x.extract(1, x.length() - 1)).stream().map(tensor -> tensor.Get(0)));
        Tensor subtract1 = y.extract(0, x.length() - 2).subtract(y.extract(1, y.length() - 1));
        Tensor add = subtract1.pmul(subtract1).add(subtract.pmul(subtract));
        double step = Tensors.isEmpty(add) //
            ? 0
            : Math.sqrt(Mean.of(add).Get().number().doubleValue());
        Tensor beta = LeastSquares.of(x, y);
        double angle = Math.atan(beta.Get(0).number().doubleValue());
        return Last.of(nonEmptyMeans).add(AngleVector.of(RealScalar.of(angle)).multiply(RealScalar.of(i * step)));
      }
      return Flatten.of(nonEmptyMeans); // if only one point assume it is not going to move
    }
    return Tensors.empty();
  }

  // assign last hull shape to the new mean
  private void predictNextHull(Tensor hull) {
    if (Tensors.nonEmpty(hull)) {
      Tensor mean = Mean.of(hull);
      Tensor next = Tensors.empty();
      Tensor twiceNext = Tensors.empty();
      Tensor nm = Last.of(nextMeans);
      Tensor tnm = Last.of(twiceNextMeans);
      if (Tensors.nonEmpty(Last.of(nm))) {
        for (Tensor x : hull) {
          Tensor y = x.subtract(mean);
          next.append(nm.add(y));
          twiceNext.append(tnm.add(y));
        }
        nextHulls.append(next);
        twiceNextHulls.append(twiceNext);
      }
    }
  }

  public Tensor getMeanPredictions() {
    return nextMeans.unmodifiable();
  }

  public Tensor getHullPredictions() {
    return nextHulls.unmodifiable();
  }

  public Tensor getTNMeanPredictions() {
    return twiceNextMeans.unmodifiable();
  }

  public Tensor getTNHullPredictions() {
    return twiceNextHulls.unmodifiable();
  }
}
