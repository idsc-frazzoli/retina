// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import java.io.Serializable;

import ch.ethz.idsc.gokart.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public abstract class AbstractSteerMapping implements SteerMapping, Serializable {
  private final ScalarUnaryOperator column2steer;
  private final ScalarUnaryOperator steer2column;

  protected AbstractSteerMapping(ScalarUnaryOperator column2steer, ScalarUnaryOperator steer2column) {
    this.column2steer = column2steer;
    this.steer2column = steer2column;
  }

  @Override // from SteerMapping
  public final Scalar getRatioFromSCE(SteerColumnInterface steerColumnInterface) {
    return getRatioFromSCE(steerColumnInterface.getSteerColumnEncoderCentered());
  }

  @Override // from SteerMapping
  public final Scalar getRatioFromSCE(Scalar scalar) {
    return column2steer.apply(scalar);
  }

  @Override // from SteerMapping
  public final Scalar getSCEfromRatio(Scalar ratio) {
    return steer2column.apply(ratio);
  }
}
