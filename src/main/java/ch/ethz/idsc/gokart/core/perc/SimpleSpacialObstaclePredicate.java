// code by vc, jph
package ch.ethz.idsc.gokart.core.perc;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Clip;

/** class interprets 3d-points in lidar coordinates and corrects for an inclination of the lidar.
 * the implementation makes use of the approximation sin(incline) ~ incline for small incline
 * 
 * the purpose of the class is to carry out the math for the simple obstacle check method
 * and filter out points that belong to the floor */
public class SimpleSpacialObstaclePredicate implements SpacialXZObstaclePredicate {
  private final float min;
  private final float max;
  private final float incline;

  /** @param range along z-axis
   * @param incline rotation around y-axis */
  public SimpleSpacialObstaclePredicate(Clip range, Scalar incline) {
    min = range.min().number().floatValue();
    max = range.max().number().floatValue();
    this.incline = incline.number().floatValue();
  }

  @Override // from SpacialObstaclePredicate
  public boolean isObstacle(Tensor point) {
    return isObstacle( //
        point.Get(0).number().floatValue(), //
        point.Get(2).number().floatValue());
  }

  @Override // from SpacialXZObstaclePredicate
  public boolean isObstacle(float x, float z) {
    double z_corrected = z - x * incline; // negative sign
    return min < z_corrected //
        && z_corrected < max;
  }
}
