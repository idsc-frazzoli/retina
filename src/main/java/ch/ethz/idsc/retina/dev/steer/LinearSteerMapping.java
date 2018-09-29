// code by jph
package ch.ethz.idsc.retina.dev.steer;

import java.util.Objects;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.UnitSystem;

public class LinearSteerMapping implements SteerMapping {
  private final Scalar column2steer;

  public LinearSteerMapping(Scalar column2steer) {
    this.column2steer = Objects.requireNonNull(column2steer);
  }

  @Override // from SteerMapping
  public Scalar getAngleFromSCE(SteerColumnInterface steerColumnInterface) {
    return UnitSystem.SI().apply( //
        steerColumnInterface.getSteerColumnEncoderCentered().multiply(column2steer));
  }

  @Override // from SteerMapping
  public Scalar getSCEfromAngle(Scalar angle) {
    return angle.divide(column2steer);
  }
}
