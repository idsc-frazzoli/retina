// code by ynager
package ch.ethz.idsc.gokart.core.map;

import java.io.Serializable;

import ch.ethz.idsc.retina.sys.AppResources;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

/** parameters for the mapping of the gokart surroundings and obstacles */
public class MappingConfig implements Serializable {
  public static final MappingConfig GLOBAL = AppResources.load(new MappingConfig());
  /***************************************************/
  /** TODO comment on interpretation */
  public Scalar P_M = DoubleScalar.of(0.5);
  /** TODO comment on interpretation */
  public Scalar P_M_HIT = DoubleScalar.of(0.85);
  /** TODO comment on interpretation */
  public Scalar P_THRESH = DoubleScalar.of(0.5);
  /** TODO comment on interpretation */
  public Scalar obsRadius = Quantity.of(1.5, SI.METER);

  /***************************************************/
  public double getP_M() {
    return P_M.number().doubleValue();
  }

  public double getP_M_HIT() {
    return P_M_HIT.number().doubleValue();
  }

  public double getP_THRESH() {
    return P_THRESH.number().doubleValue();
  }
}
