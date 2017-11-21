// code by jph
package ch.ethz.idsc.retina.dev.steer;

import ch.ethz.idsc.retina.util.math.IntervalTracker;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

/** the magic constants defined in the class were found by experimentation
 * 
 * the most safety critical constant is HARD
 * it targets the case when the steering range interval exceeds all
 * previously observed values. in this driving may be dangerous because
 * for instance the concept of "straight ahead", i.e. angle == 0
 * cannot be commanded by the hardware.
 * Therefore, the case when the width of the interval tracker exceeds
 * the HARD threshold is considered an emergency */
public final class SteerColumnTracker implements SteerGetListener {
  public static final Scalar MAX_SCE = Quantity.of(0.6743167638778687, SteerPutEvent.UNIT_ENCODER);
  private static final double SOFT = 1.45;
  private static final double HARD = 1.6; // measured 1.538
  // ---
  private final IntervalTracker intervalTracker = new IntervalTracker();

  @Override
  public void getEvent(SteerGetEvent steerGetEvent) {
    intervalTracker.setValue(steerGetEvent.getGcpRelRckPos());
  }

  /** @return true if steering is operational
   * Important: the upper bound should be checked in an emergency module */
  public boolean isCalibrated() {
    double width = intervalTracker.getWidth();
    return SOFT < width;
  }

  public boolean isCalibratedAndHealthy() {
    return isCalibrated() && intervalTracker.getWidth() < HARD;
  }

  public double getIntervalWidth() {
    return intervalTracker.getWidth();
  }

  /** @return value centered around 0 zero means driving straight */
  // TODO NRJ document sign means left/right
  public Scalar getEncoderValueCentered() {
    if (!isCalibrated())
      throw new RuntimeException();
    return Quantity.of(intervalTracker.getValueCentered(), SteerPutEvent.UNIT_ENCODER);
  }
}
