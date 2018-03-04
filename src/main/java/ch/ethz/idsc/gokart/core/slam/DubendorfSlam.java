// code by jph
package ch.ethz.idsc.gokart.core.slam;

import ch.ethz.idsc.owl.math.Degree;
import ch.ethz.idsc.tensor.RealScalar;

/** the maximum turning rate that was observed is 180[deg/s]. the lidar has
 * a rate of 20[Hz] which means a maximum rotation of 180/20[deg] == 9[deg].
 * 
 * using a resolution of 1.5 for the angle, allows to correct an error
 * in the gyro of 1.5[deg] * 2.176 == 3.264[deg]
 * 
 * 1 + 0.6 + 0.6^2 + 0.6^3 == 2.176 */
public enum DubendorfSlam {
  ;
  /** during operation, only 3-5 levels should be used */
  public static final Se2MultiresGrids SE2MULTIRESGRIDS = //
      new Se2MultiresGrids(RealScalar.of(0.5), Degree.of(1.5), 1, 4);
}
