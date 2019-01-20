// code by mh
package ch.ethz.idsc.gokart.core.tvec;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AppResources;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

/** parameters for PI controller of torque control */
public class TorqueVectoringConfig {
  public static final TorqueVectoringConfig GLOBAL = AppResources.load(new TorqueVectoringConfig());
  /***************************************************/
  /** The Static compensation coefficient */
  public Scalar staticCompensation = Quantity.of(0.8, SI.ACCELERATION.negate());
  /** The Dynamic correction coefficient */
  public Scalar dynamicCorrection = Quantity.of(1.5, SI.SECOND);
  /** The Dynamic correction coefficient */
  public Scalar staticPrediction = Quantity.of(0.5, SI.ANGULAR_ACCELERATION.negate());
  /** TODO document control constant used in ITV */
  public Scalar ks = Quantity.of(10.0, SI.SECOND);
  /** Scaling factor for Normalized torque vectoring */
  public Scalar kn = Quantity.of(1, SI.ACCELERATION.negate());
}
