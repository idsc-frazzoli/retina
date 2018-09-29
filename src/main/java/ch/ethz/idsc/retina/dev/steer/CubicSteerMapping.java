// code by mh
package ch.ethz.idsc.retina.dev.steer;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Series;
import ch.ethz.idsc.tensor.qty.UnitSystem;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public class CubicSteerMapping implements SteerMapping {
  private final ScalarUnaryOperator column2steer;
  private final ScalarUnaryOperator steer2column;

  public CubicSteerMapping( //
      Scalar column2steer1, Scalar column2steer3, //
      Scalar steer2column1, Scalar steer2column3) {
    column2steer = Series.of(Tensors.of(RealScalar.ZERO, column2steer1, RealScalar.ZERO, column2steer3));
    steer2column = Series.of(Tensors.of(RealScalar.ZERO, steer2column1, RealScalar.ZERO, steer2column3));
  }

  @Override // from SteerMapping
  public Scalar getAngleFromSCE(SteerColumnInterface steerColumnInterface) {
    Scalar sce = steerColumnInterface.getSteerColumnEncoderCentered();
    return UnitSystem.SI().apply(column2steer.apply(sce));
  }

  @Override // from SteerMapping
  public Scalar getSCEfromAngle(Scalar angle) {
    return steer2column.apply(UnitSystem.SI().apply(angle));
  }
}
