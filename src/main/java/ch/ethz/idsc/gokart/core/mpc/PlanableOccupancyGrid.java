package ch.ethz.idsc.gokart.core.mpc;

import java.awt.Point;

import ch.ethz.idsc.tensor.Tensor;

public interface PlanableOccupancyGrid {
  Tensor getGridSize();

  boolean isCellOccupied(Point points);

  Tensor getTransform();
}
