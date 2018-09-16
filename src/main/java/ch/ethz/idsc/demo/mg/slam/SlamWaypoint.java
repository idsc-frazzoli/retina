// code by mg
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Primitives;

/** way point object for SLAM algorithm */
public class SlamWaypoint {
  private final Tensor worldPosition;
  /** visibility given the current pose of the go kart */
  private final boolean visibility;

  /** @param worldPosition interpreted as [m]
   * @param visibility */
  public SlamWaypoint(Tensor worldPosition, boolean visibility) {
    this.worldPosition = worldPosition;
    this.visibility = visibility;
  }

  public double[] getWorldPosition() {
    return Primitives.toDoubleArray(worldPosition);
  }

  public boolean isVisible() {
    return visibility;
  }
}
