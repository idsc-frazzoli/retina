// code by mg
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Primitives;
import ch.ethz.idsc.tensor.mat.Inverse;

/** way point object */
public class WayPoint {
  private final double[] worldPosition;
  private Tensor gokartPosition;
  /** visibility given the current pose of the go kart */
  private boolean visibility;

  /** @param worldPosition interpreted as [m]
   * @param pose unitless representation */
  public WayPoint(double[] worldPosition, Tensor pose) {
    this.worldPosition = worldPosition;
    computeGokartPosition(pose);
  }

  /** @param pose unitless representation */
  private void computeGokartPosition(Tensor pose) {
    GeometricLayer worldToGokartLayer = GeometricLayer.of(Inverse.of(Se2Utils.toSE2Matrix(pose)));
    gokartPosition = worldToGokartLayer.toVector(worldPosition[0], worldPosition[1]);
  }

  public double[] getWorldPosition() {
    return worldPosition;
  }

  /** @param pose unitless representation
   * @return */
  public double[] getGokartPosition(Tensor pose) {
    computeGokartPosition(pose);
    return Primitives.toDoubleArray(gokartPosition);
  }

  public boolean getVisibility() {
    return visibility;
  }

  public void setGokartPosition(Tensor gokartPosition) {
    this.gokartPosition = gokartPosition;
  }

  public void setVisibility(boolean visibility) {
    this.visibility = visibility;
  }
}
