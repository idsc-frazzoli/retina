// code by mh
package ch.ethz.idsc.gokart.core.track;

import ch.ethz.idsc.tensor.Tensor;

/* package */ abstract class TrackConstraint {
  private Tensor controlPointsX = null;
  private Tensor controlPointsY = null;
  private Tensor radiusControlPoints = null;

  protected final void setAll(Tensor controlPointsX, Tensor controlPointsY, Tensor radiusControlPoints) {
    this.controlPointsX = controlPointsX;
    this.controlPointsY = controlPointsY;
    this.radiusControlPoints = radiusControlPoints;
  }

  public final Tensor getControlPointsX() {
    return controlPointsX;
  }

  public final Tensor getControlPointsY() {
    return controlPointsY;
  }

  public final Tensor getRadiusControlPoints() {
    return radiusControlPoints;
  }

  public abstract void compute(Tensor controlpointsX, Tensor controlpointsY, Tensor radiusControlPoints);
}