// code by mg
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Primitives;
import ch.ethz.idsc.tensor.mat.Inverse;

/** way point object for SLAM algorithm */
public class SlamWayPoint {
  private final double[] worldPosition;
  /** visibility given the current pose of the go kart */
  // TODO MG make class design so that visibility is final
  private boolean visibility;

  /** @param worldPosition interpreted as [m]
   * @param pose unitless representation */
  // TODO MG parameter Tensor pose currently not needed... talk to Jan
  public SlamWayPoint(double[] worldPosition, Tensor pose) {
    this.worldPosition = worldPosition;
  }

  /** @param pose unitless representation
   * @return */
  private Tensor computeGokartPosition(Tensor pose) {
    GeometricLayer worldToGokartLayer = GeometricLayer.of(Inverse.of(Se2Utils.toSE2Matrix(pose)));
    return worldToGokartLayer.toVector(worldPosition[0], worldPosition[1]);
  }

  public double[] getWorldPosition() {
    return worldPosition;
  }

  /** @param pose unitless representation
   * @return position of event in go kart frame given the pose */
  public double[] getGokartPosition(Tensor pose) {
    Tensor gokartPosition = computeGokartPosition(pose);
    return Primitives.toDoubleArray(gokartPosition);
  }

  public boolean getVisibility() {
    return visibility;
  }

  public void setVisibility(boolean visibility) {
    this.visibility = visibility;
  }
}
