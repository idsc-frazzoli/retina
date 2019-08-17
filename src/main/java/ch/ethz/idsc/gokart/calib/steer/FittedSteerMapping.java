// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Series;
import ch.ethz.idsc.tensor.qty.Quantity;

/** the constants in the fitted model were derived in the report
 * 20190509_steering_turning_ratio
 * 
 * the mapping {@link #getSCEfromRatio(Scalar)} is defined for input value ratios in the interval
 * [-0.5[m^-1], +0.5[m^-1]]. for values outside this interval, the return value produces a complex number! */
public class FittedSteerMapping extends AbstractSteerMapping {
  private static final SteerMapping INSTANCE = new FittedSteerMapping( //
      Quantity.of(+0.8284521034333863, "SCE^-1*m^-1"), Quantity.of(-0.33633373640449604, "SCE^-3*m^-1"), //
      Quantity.of(+1.1188984658584833, "SCE*m"), Quantity.of(+2.5021535682516487, "SCE*m^3"), Quantity.of(-4.302540536223425, "SCE*m^5"));

  public static SteerMapping instance() {
    return INSTANCE;
  }

  // ---
  private FittedSteerMapping( //
      Scalar column2steer1, Scalar column2steer3, //
      Scalar steer2column1, Scalar steer2column3, Scalar steer2column5) {
    super( //
        Series.of(Tensors.of(RealScalar.ZERO, column2steer1, RealScalar.ZERO, column2steer3)), //
        new InverseSteerCubic(column2steer1, column2steer3));
  }
}
