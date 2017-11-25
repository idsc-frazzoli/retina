// code by ej
package ch.ethz.idsc.retina.dev.steer;

import ch.ethz.idsc.retina.dev.zhkart.AutoboxCalibrationProvider;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.sca.Sin;

public class SteerCalibrationProvider extends AutoboxCalibrationProvider<SteerPutEvent> {
  public static final SteerCalibrationProvider INSTANCE = new SteerCalibrationProvider();
  // private static final Scalar HALF = RealScalar.of(0.5);

  // ---
  private SteerCalibrationProvider() {
  }

  @Override
  protected void protected_schedule() {
    final long timestamp = now_ms();
    final Scalar full = SteerConfig.GLOBAL.calibration;
    // final Scalar half = full.multiply(HALF);
    final int oneside_ms = 3000;
    final int resolution = 20;
    Tensor times = Subdivide.of(100, oneside_ms, resolution); // seconds
    Tensor sampl = Subdivide.of(0, Math.PI * 0.5, resolution); // seconds
    Tensor ampli = Sin.of(sampl).multiply(full);
    for (int index = 0; index < times.length(); ++index)
      eventUntil( //
          timestamp + times.Get(index).number().intValue(), //
          SteerPutEvent.createOn(ampli.Get(index)));
    for (int index = 0; index < times.length(); ++index)
      eventUntil( //
          timestamp + times.Get(index).number().intValue() + oneside_ms, //
          SteerPutEvent.createOn(ampli.Get(index).negate()));
    // eventUntil(timestamp += 1000, SteerPutEvent.createOn(half));
    // eventUntil(timestamp += 1000, SteerPutEvent.createOn(full));
    // eventUntil(timestamp += 2000, SteerPutEvent.createOn(half.negate()));
    // eventUntil(timestamp += 1000, SteerPutEvent.createOn(full.negate()));
    // eventUntil(timestamp += 500, SteerPutEvent.createOn(half));
  }
}
