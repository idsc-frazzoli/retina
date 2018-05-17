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
  public Scalar horizonDistance = RealScalar.of(10);
  /***************************************************/
}
