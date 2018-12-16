// code by ej and jph
package ch.ethz.idsc.gokart.dev.steer;

import ch.ethz.idsc.gokart.core.AutoboxCalibrationProvider;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.sca.Sin;

/** applies torque to the steering column in order to
 * first more the steering all the way to the left,
 * and then all the way to the right.
 * the process takes approximately 6 sec. */
public class SteerCalibrationProvider extends AutoboxCalibrationProvider<SteerPutEvent> {
  public static final SteerCalibrationProvider INSTANCE = new SteerCalibrationProvider();

  // ---
  private SteerCalibrationProvider() {
  }

  @Override // from AutoboxCalibrationProvider
  protected void protected_schedule() {
    final long timestamp = now_ms();
    final int oneside_ms = 3000;
    final int resolution = 20;
    Tensor times = Subdivide.of(100, oneside_ms, resolution); // seconds
    Tensor sampl = Subdivide.of(0, Math.PI * 0.5, resolution); // seconds
    Tensor ampli = Sin.of(sampl).multiply(SteerConfig.GLOBAL.calibration);
    for (int index = 0; index < times.length(); ++index) {
      int _index = index;
      eventUntil( //
          timestamp + times.Get(index).number().intValue(), //
          () -> SteerPutEvent.createOn(ampli.Get(_index)));
    }
    for (int index = 0; index < times.length(); ++index) {
      int _index = index;
      eventUntil( //
          timestamp + times.Get(index).number().intValue() + oneside_ms, //
          () -> SteerPutEvent.createOn(ampli.Get(_index).negate()));
    }
  }

  @Override // from AutoboxCalibrationProvider
  protected boolean hintScheduleRequired() {
    return !SteerSocket.INSTANCE.getSteerColumnTracker().isSteerColumnCalibrated();
  }
}
