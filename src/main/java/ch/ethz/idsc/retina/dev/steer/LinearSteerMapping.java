// code by jph
package ch.ethz.idsc.retina.dev.steer;

import java.util.Objects;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.UnitSystem;

/** the linear steer mapping was in use from 2017-12 until at least 2018-09 */
public class LinearSteerMapping implements SteerMapping {
  /** conversion factor from measured steer column angle to front wheel angle */
  public static final Scalar COLUMN_TO_STEER = Quantity.of(0.6, "rad*SCE^-1");
  private static final SteerMapping INSTANCE = new LinearSteerMapping(COLUMN_TO_STEER);

  public static SteerMapping instance() {
    return INSTANCE;
  }

  // ---
  private final Scalar column2steer;

  private LinearSteerMapping(Scalar column2steer) {
    this.column2steer = Objects.requireNonNull(column2steer);
  }

  @Override // from SteerMapping
  public Scalar getAngleFromSCE(SteerColumnInterface steerColumnInterface) {
    return UnitSystem.SI().apply( //
        steerColumnInterface.getSteerColumnEncoderCentered().multiply(column2steer));
  }

  @Override // from SteerMapping
  public Scalar getSCEfromAngle(Scalar angle) {
    return UnitSystem.SI().apply(angle.divide(column2steer));
  }
}
