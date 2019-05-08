// code by mh, jph
package ch.ethz.idsc.gokart.calib.steer;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Series;
import ch.ethz.idsc.tensor.qty.Quantity;

/** based on report
 * https://github.com/idsc-frazzoli/retina/files/2440459/20181001_steering_measurement.pdf */
public class CubicSteerMapping extends AbstractSteerMapping {
  /** DO NOT MODIFY CONSTANTS BUT CREATE SECOND VERSION IF NEEDED */
  private static final SteerMapping INSTANCE = new CubicSteerMapping( //
      Quantity.of(+0.9189766407706671, "SCE^-1*m^-1"), Quantity.of(-0.5606503091815459, "SCE^-3*m^-1"), //
      Quantity.of(+0.9755773866318296, "SCE*m"), Quantity.of(+2.325797449027361, "SCE*m^3"));

  public static SteerMapping instance() {
    return INSTANCE;
  }

  // ---
  private CubicSteerMapping( //
      Scalar column2steer1, Scalar column2steer3, //
      Scalar steer2column1, Scalar steer2column3) {
    super( //
        Series.of(Tensors.of(RealScalar.ZERO, column2steer1, RealScalar.ZERO, column2steer3)), //
        Series.of(Tensors.of(RealScalar.ZERO, steer2column1, RealScalar.ZERO, steer2column3)));
  }
}
