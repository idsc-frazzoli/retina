// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.tensor.Tensor;

// TODO MH document interface functions
public interface PlanableOccupancyGrid extends Region<Tensor> {
  Tensor getGridSize();

  boolean isCellOccupied(int x, int y);

  Tensor getTransform();
}
