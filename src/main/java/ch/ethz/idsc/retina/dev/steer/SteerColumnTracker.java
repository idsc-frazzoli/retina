// code by jph
package ch.ethz.idsc.retina.dev.steer;

import ch.ethz.idsc.retina.util.math.IntervalTracker;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

public final class SteerColumnTracker implements SteerGetListener {
  /** value found by experimentation */
  public static final Scalar MAX_SCE = Quantity.of(0.6743167638778687, SteerPutEvent.UNIT_ENCODER);
  /** values found by experimentation */
  private static final double SOFT = 1.357;
  /** on the ground, the former threshold was exceeded */
  private static final double HARD = 1.7; // 1.405; // FIXME there are more magic const in the code below
  // ---
  private final IntervalTracker intervalTracker = new IntervalTracker();

  @Override
  public void getEvent(SteerGetEvent steerGetEvent) {
    intervalTracker.setValue(steerGetEvent.getGcpRelRckPos());
  }

  /** @return true if steering is operational */
  public boolean isCalibrated() {
    double width = intervalTracker.getWidth();
    return SOFT - 0.01 < width && width < HARD + 0.10; // <- 0.05 is insufficient
  }

  /** @return value centered around 0
   * zero means driving straight */
  // TODO NRJ document sign means left/right
  public Scalar getSteeringValue() {
    if (!isCalibrated())
      throw new RuntimeException();
    return Quantity.of(intervalTracker.getValueCentered(), SteerPutEvent.UNIT_ENCODER);
  }
}
