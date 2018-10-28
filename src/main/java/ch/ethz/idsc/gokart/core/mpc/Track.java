package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.tensor.Tensor;

public interface Track {
  Tensor getMiddleLine(int resolution);

  Tensor getLeftLine(int resolution);

  Tensor getRightLine(int resolution);

  Tensor getNearestPosition(Tensor position);

  boolean isInTrack(Tensor position);
}
