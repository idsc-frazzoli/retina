// code by ej
package ch.ethz.idsc.retina.dev.steer;

import ch.ethz.idsc.retina.dev.zhkart.AutoboxCalibrationProvider;

public class SteerCalibrationProvider extends AutoboxCalibrationProvider<SteerPutEvent> {
  public static final SteerCalibrationProvider INSTANCE = new SteerCalibrationProvider();

  // ---
  private SteerCalibrationProvider() {
  }

  @Override
  protected void protected_schedule() {
    long timestamp = now();
    // TODO this torque is magic constant, change it
    final double full = 0.2;
    final double half = full * 0.5;
    eventUntil(timestamp += 1000, new SteerPutEvent(SteerPutEvent.CMD_ON, half));
    eventUntil(timestamp += 1000, new SteerPutEvent(SteerPutEvent.CMD_ON, full));
    eventUntil(timestamp += 2000, new SteerPutEvent(SteerPutEvent.CMD_ON, -half));
    eventUntil(timestamp += 1000, new SteerPutEvent(SteerPutEvent.CMD_ON, -full));
    eventUntil(timestamp += 500, new SteerPutEvent(SteerPutEvent.CMD_ON, half));
  }
}
