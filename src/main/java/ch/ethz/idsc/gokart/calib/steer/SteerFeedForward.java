// code by am, jph
package ch.ethz.idsc.gokart.calib.steer;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** torque needed to maintain position of steering column
 * 
 * maps a quantity of unit SCE to a quantity of unit SCE */
// TODO AM generate report that justifies the numbers
public enum SteerFeedForward implements ScalarUnaryOperator {
  FUNCTION;
  // ---
  private final ScalarUnaryOperator series = SteerFeedForwardConfig.GLOBAL.series();

  @Override
  public Scalar apply(Scalar column) {
    return series.apply(column);
  }
}
