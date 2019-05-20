// code by am, jph
package ch.ethz.idsc.gokart.calib.steer;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Series;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** torque needed to maintain position of steering column
 * 
 * maps a quantity of unit SCE to a quantity of unit SCE */
// TODO AM generate report that justifies the numbers
public enum SteerFeedForward implements ScalarUnaryOperator {
  FUNCTION;
  // ---
  private final ScalarUnaryOperator series = Series.of(Tensors.of( //
      RealScalar.ZERO, //
      Quantity.of(+0.968725, "SCT*SCE^-1"), //
      RealScalar.ZERO, //
      Quantity.of(-0.414766, "SCT*SCE^-3")));

  @Override
  public Scalar apply(Scalar column) {
    return series.apply(column);
  }
}
