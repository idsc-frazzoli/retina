// code by ynager
package ch.ethz.idsc.gokart.core.pure;

import java.io.Serializable;

import ch.ethz.idsc.retina.sys.AppResources;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

/** 
 * */
public class TrajectoryConfig implements Serializable {
  public static final TrajectoryConfig GLOBAL = AppResources.load(new TrajectoryConfig());
  /***************************************************/
  public Scalar planningPeriod = Quantity.of(1, SI.SECOND); // 1[s] == 1[Hz]
  public Scalar horizonDistance = RealScalar.of(8);
  /** rotation per meter driven is at least 23[deg/m]
   * 20180429_minimum_turning_radius.pdf
   * 20180517 reduced value to 20[deg/m] to be more conservative and avoid extreme steering */
  public Scalar maxRotation = Quantity.of(20, "deg*m^-1");
  /***************************************************/
}
