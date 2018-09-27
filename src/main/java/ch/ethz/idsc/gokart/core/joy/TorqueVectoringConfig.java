// code by mh
package ch.ethz.idsc.gokart.core.joy;

import java.io.Serializable;

import ch.ethz.idsc.retina.sys.AppResources;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

/** parameters for PI controller of torque control */
public class TorqueVectoringConfig implements Serializable {
  public static final TorqueVectoringConfig GLOBAL = AppResources.load(new TorqueVectoringConfig());
  /***************************************************/
  /** */
  public Scalar staticCompensation = Quantity.of(0.5, "s^2*m^-1");
  /**  */
  public Scalar dynamicCorrection = Quantity.of(1, "s");
}
