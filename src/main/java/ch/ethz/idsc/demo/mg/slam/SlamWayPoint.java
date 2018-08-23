// code by mg
package ch.ethz.idsc.demo.mg.slam;

/** way point object for SLAM algorithm */
public class SlamWayPoint {
  private final double[] worldPosition;
  /** visibility given the current pose of the go kart */
  private final boolean visibility;

  /** @param worldPosition interpreted as [m] */
  public SlamWayPoint(double[] worldPosition, boolean visibility) {
    this.worldPosition = worldPosition;
    this.visibility = visibility;
  }

  public double[] getWorldPosition() {
    return worldPosition;
  }

  public boolean getVisibility() {
    return visibility;
  }
}
