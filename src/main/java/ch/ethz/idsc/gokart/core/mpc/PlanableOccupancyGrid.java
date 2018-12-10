// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.awt.Point;

import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.tensor.Tensor;

// TODO MH document interface functions
public interface PlanableOccupancyGrid extends Region<Tensor> {
  Tensor getGridSize();

  // TODO MH I suggest that function takes 2 separate int's as input: int x, int y
  boolean isCellOccupied(Point points);

  Tensor getTransform();
}
