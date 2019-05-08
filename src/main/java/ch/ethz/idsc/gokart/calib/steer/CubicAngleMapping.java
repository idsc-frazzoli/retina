// code by mh, jph
package ch.ethz.idsc.gokart.calib.steer;

import ch.ethz.idsc.gokart.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Series;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** based on report
 * https://github.com/idsc-frazzoli/retina/files/2440459/20181001_steering_measurement.pdf */
public class CubicAngleMapping implements AngleMapping {
  /** DO NOT MODIFY CONSTANTS BUT CREATE SECOND VERSION IF NEEDED */
  private static final AngleMapping INSTANCE = new CubicAngleMapping( //
      Quantity.of(+0.9189766407706671, "SCE^-1"), Quantity.of(-0.5606503091815459, "SCE^-3"), //
      Quantity.of(+0.9755773866318296, "SCE"), Quantity.of(+2.325797449027361, "SCE"));

  public static AngleMapping instance() {
    return INSTANCE;
  }

  // ---
  private final ScalarUnaryOperator column2steer;
  private final ScalarUnaryOperator steer2column;

  private CubicAngleMapping( //
      Scalar column2steer1, Scalar column2steer3, //
      Scalar steer2column1, Scalar steer2column3) {
    column2steer = Series.of(Tensors.of(RealScalar.ZERO, column2steer1, RealScalar.ZERO, column2steer3));
    steer2column = Series.of(Tensors.of(RealScalar.ZERO, steer2column1, RealScalar.ZERO, steer2column3));
  }

  @Override // from AngleMapping
  public Scalar getAngleFromSCE(SteerColumnInterface steerColumnInterface) {
    return getAngleFromSCE(steerColumnInterface.getSteerColumnEncoderCentered());
  }

  @Override // from AngleMapping
  public Scalar getAngleFromSCE(Scalar scalar) {
    return column2steer.apply(scalar);
  }

  @Override // from AngleMapping
  public Scalar getSCEfromAngle(Scalar angle) {
    return steer2column.apply(angle);
  }
}
