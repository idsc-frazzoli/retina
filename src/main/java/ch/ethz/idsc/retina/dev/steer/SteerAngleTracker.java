// code by jph
package ch.ethz.idsc.retina.dev.steer;

import ch.ethz.idsc.retina.util.math.IntervalTracker;

public final class SteerAngleTracker implements SteerGetListener {
  /** value found by experimentation */
  public static final double MAX_ANGLE = 0.6743167638778687; // TODO UNITless, should use our own fake unit
  /** values found by experimentation */
  private static final double SOFT = 1.357;
  private static final double HARD = 1.405;
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
  public double getSteeringValue() { // TODO should use Quantity
    if (!isCalibrated())
      throw new RuntimeException();
    return intervalTracker.getValueCentered();
  }
}
