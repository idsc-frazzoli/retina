// code by ej
package ch.ethz.idsc.retina.dev.steer;

import ch.ethz.idsc.retina.dev.zhkart.AutoboxCalibrationProvider;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

public class SteerCalibrationProvider extends AutoboxCalibrationProvider<SteerPutEvent> {
  public static final SteerCalibrationProvider INSTANCE = new SteerCalibrationProvider();
  private static final Scalar HALF = RealScalar.of(0.5);

  // ---
  private SteerCalibrationProvider() {
  }

  @Override
  protected void protected_schedule() {
    long timestamp = now();
    final Scalar full = SteerConfig.GLOBAL.calibration;
    final Scalar half = full.multiply(HALF);
    eventUntil(timestamp += 1000, SteerPutEvent.createOn(half));
    eventUntil(timestamp += 1000, SteerPutEvent.createOn(full));
    eventUntil(timestamp += 2000, SteerPutEvent.createOn(half.negate()));
    eventUntil(timestamp += 1000, SteerPutEvent.createOn(full.negate()));
    eventUntil(timestamp += 500, SteerPutEvent.createOn(half));
  }
}
