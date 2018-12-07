// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.awt.Point;

import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.tensor.Tensor;

public interface PlanableOccupancyGrid extends Region<Tensor> {
  Tensor getGridSize();

  boolean isCellOccupied(Point points);

  Tensor getTransform();
}
