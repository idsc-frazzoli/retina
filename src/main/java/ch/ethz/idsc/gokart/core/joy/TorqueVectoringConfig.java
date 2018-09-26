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
  public Scalar SteeringCorrection = Quantity.of(1, SI.PER_METER);
  /** */
  public Scalar StaticCompensation = Quantity.of(1, SI.ONE);
  /**  */
  public Scalar DynamicCorrection = Quantity.of(1, SI.ONE);
  /** */
  public Scalar 
}
