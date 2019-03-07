// code by mh, jph
package ch.ethz.idsc.gokart.calib.steer;

import ch.ethz.idsc.gokart.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Series;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.UnitSystem;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public class CubicSteerMapping implements SteerMapping {
  /** DO NOT MODIFY CONSTANTS BUT CREATE SECOND VERSION */
  private static final SteerMapping APPROXIMATION_1 = new CubicSteerMapping( //
      Quantity.of(+0.9189766407706671, "rad*SCE^-1"), Quantity.of(-0.5606503091815459, "rad*SCE^-3"), //
      Quantity.of(+0.9755773866318296, "SCE"), Quantity.of(+2.325797449027361, "SCE"));

  public static SteerMapping approximation_1() {
    return APPROXIMATION_1;
  }

  // ---
  private final ScalarUnaryOperator column2steer;
  private final ScalarUnaryOperator steer2column;

  private CubicSteerMapping( //
      Scalar column2steer1, Scalar column2steer3, //
      Scalar steer2column1, Scalar steer2column3) {
    column2steer = Series.of(Tensors.of(RealScalar.ZERO, column2steer1, RealScalar.ZERO, column2steer3));
    steer2column = Series.of(Tensors.of(RealScalar.ZERO, steer2column1, RealScalar.ZERO, steer2column3));
  }

  @Override // from SteerMapping
  public Scalar getAngleFromSCE(SteerColumnInterface steerColumnInterface) {
    return getAngleFromSCE(steerColumnInterface.getSteerColumnEncoderCentered());
  }

  @Override // from SteerMapping
  public Scalar getAngleFromSCE(Scalar scalar) {
    return UnitSystem.SI().apply(column2steer.apply(scalar));
  }

  @Override // from SteerMapping
  public Scalar getSCEfromAngle(Scalar angle) {
    return steer2column.apply(UnitSystem.SI().apply(angle));
  }
}
