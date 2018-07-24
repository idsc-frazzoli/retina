// code by mg
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.tensor.Tensor;

// waypoint object
public class WayPoint {
  private final double[] worldPosition;
  private double[] gokartPosition;
  private boolean currentlyVisible; // visibility given the current pose of the go kart

  public WayPoint(double[] worldPosition) {
    this.worldPosition = worldPosition;
    gokartPosition = new double[2];
  }

  public double[] getWorldPosition() {
    return worldPosition;
  }

  public double[] getGokartPosition() {
    return gokartPosition;
  }

  public boolean getVisibility() {
    return currentlyVisible;
  }

  public void setGokartPosition(Tensor gokartPosition) {
    this.gokartPosition[0] = gokartPosition.Get(0).number().doubleValue();
    this.gokartPosition[1] = gokartPosition.Get(1).number().doubleValue();
  }

  public void setVisibility(boolean visibility) {
    currentlyVisible = visibility;
  }
}
