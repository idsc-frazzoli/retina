package ch.ethz.idsc.gokart.core.ekf;

import ch.ethz.idsc.tensor.Tensor;

public interface VelocityEstimation {
  Tensor getVelocity();
}
