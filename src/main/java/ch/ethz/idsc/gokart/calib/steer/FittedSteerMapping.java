// code by mh, jph
package ch.ethz.idsc.gokart.calib.steer;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Series;
import ch.ethz.idsc.tensor.qty.Quantity;

/** 20190507 based on data collection */
public class FittedSteerMapping extends AbstractSteerMapping {
  /** DO NOT MODIFY CONSTANTS BUT CREATE SECOND VERSION IF NEEDED */
  private static final SteerMapping INSTANCE = new FittedSteerMapping( //
      Quantity.of(+0.8284521034333863, "SCE^-1"), Quantity.of(-0.33633373640449604, "SCE^-3"), //
      Quantity.of(+1.1188984658584833, "SCE"), Quantity.of(+2.5021535682516487, "SCE"), Quantity.of(-4.302540536223425, "SCE"));

  public static SteerMapping instance() {
    return INSTANCE;
  }

  // ---
  private FittedSteerMapping( //
      Scalar column2steer1, Scalar column2steer3, //
      Scalar steer2column1, Scalar steer2column3, Scalar steer2column5) {
    super( //
        Series.of(Tensors.of(RealScalar.ZERO, column2steer1, RealScalar.ZERO, column2steer3)), //
        new InverseSteerCubic(column2steer1, column2steer3)
    // Series.of(Tensors.of(RealScalar.ZERO, steer2column1, RealScalar.ZERO, steer2column3, RealScalar.ZERO, steer2column5))
    );
  }
}
