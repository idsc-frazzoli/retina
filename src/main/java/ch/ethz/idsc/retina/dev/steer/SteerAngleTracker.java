// code by jph
package ch.ethz.idsc.retina.dev.steer;

import ch.ethz.idsc.retina.util.math.IntervalTracker;

public class SteerAngleTracker implements SteerGetListener {
  private static final double SOFT = 1.357;
  private static final double HARD = 1.405;
  // ---
  private final IntervalTracker intervalTracker = new IntervalTracker();

  @Override
  public void getEvent(SteerGetEvent steerGetEvent) {
    intervalTracker.setValue(steerGetEvent.getGcpRelRckPos());
  }

  public boolean isCalibrated() {
    double width = intervalTracker.getWidth();
    return SOFT - 0.01 < width && width < HARD + 0.10; // <- 0.05 is insufficient
  }

  /** @return value centered around 0
   * zero means driving straight */
  public double getSteeringValue() {
    if (!isCalibrated())
      throw new RuntimeException();
    return intervalTracker.getValueCentered();
  }

  public double getValueWithOffset() {
    if (!isCalibrated())
      throw new RuntimeException();
    return intervalTracker.getValue();
  }
}
