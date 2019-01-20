// code by jph
package ch.ethz.idsc.gokart.dev.steer;

import ch.ethz.idsc.gokart.core.fuse.SteerCalibrationWatchdog;
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
public final class SteerColumnTracker implements SteerGetListener, SteerColumnInterface {
  /** manual moving the steer from left-right leads to a range: 1.45 */
  private static final double SOFT = 1.45;
  /** upper bound on steer column interval width
   * in the lab, the max range measured: 1.538
   * on test day 2017-12-07: HARD limit increase from 1.6 to 1.9
   * on test day 2017-12-08: screw on steer column was tightened
   * on test day 2017-12-13: calibration led to range = 1.6572
   * until 2018-04-27: 1.75
   * due to investigation: 20180428_steering_unit_range.pdf
   * as of now: 1.55 */
  private static final double HARD = 1.55;
  // ---
  private final IntervalTracker intervalTracker = new IntervalTracker();
  private boolean isRelRckQual = true;

  @Override // from SteerGetListener
  public void getEvent(SteerGetEvent steerGetEvent) {
    isRelRckQual &= steerGetEvent.isRelRckQual();
    intervalTracker.setValue(steerGetEvent.getGcpRelRckPos());
  }

  @Override // from SteerColumnInterface
  public boolean isSteerColumnCalibrated() {
    return isRelRckQual //
        && SOFT < getIntervalWidth();
  }

  /** if {@link #isSteerColumnCalibrated()} returns true but {@link #isCalibratedAndHealthy()}
   * returns false, the steering and brake can still be maneuvered, but the gokart
   * should be stopped.
   * 
   * @return false if the interval tracker returns a value outside the nominal range
   * @see SteerCalibrationWatchdog */
  public boolean isCalibratedAndHealthy() {
    return isSteerColumnCalibrated() //
        && getIntervalWidth() < HARD;
  }

  /** @return */
  public double getIntervalWidth() {
    return intervalTracker.getWidth();
  }

  @Override // from SteerColumnInterface
  public Scalar getSteerColumnEncoderCentered() {
    if (!isSteerColumnCalibrated())
      throw new RuntimeException();
    return Quantity.of(intervalTracker.getValueCentered(), SteerPutEvent.UNIT_ENCODER);
  }
}
