// code by jph
package ch.ethz.idsc.gokart.core.joy;

import java.io.Serializable;

import ch.ethz.idsc.retina.sys.AppResources;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

/** parameters for PI controller of torque control */
public class TorqueVectoringConfig implements Serializable {
  public static final TorqueVectoringConfig GLOBAL = AppResources.load(new TorqueVectoringConfig());
  /***************************************************/
  /**  */
  // FIXME
  public Scalar name1 = Quantity.of(2315, NonSI.ARMS);
  /**  */
  public Scalar name2 = Quantity.of(4.0, "rad*s^-1");
  /**  */
  public Scalar name3 = Quantity.of(2.0, SI.SECOND);
  /**  */
  public Scalar name4 = Quantity.of(2.2, SI.SECOND);
}
