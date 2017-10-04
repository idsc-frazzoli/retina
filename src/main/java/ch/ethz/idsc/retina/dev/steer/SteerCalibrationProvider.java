// code by ej
package ch.ethz.idsc.retina.dev.steer;

import ch.ethz.idsc.retina.dev.zhkart.AutoboxCalibrationProvider;

public class SteerCalibrationProvider extends AutoboxCalibrationProvider<SteerPutEvent> {
  public static final SteerCalibrationProvider INSTANCE = new SteerCalibrationProvider();

  private SteerCalibrationProvider() {
  }

  public void schedule() {
    long timestamp = System.currentTimeMillis();
    // TODO this torque is magic constant, change it
    doUntil(timestamp += 1000, new SteerPutEvent(SteerPutEvent.CMD_ON, 0.1));
    doUntil(timestamp += 1000, new SteerPutEvent(SteerPutEvent.CMD_ON, 0.2));
    doUntil(timestamp += 2000, new SteerPutEvent(SteerPutEvent.CMD_ON, -0.1));
    doUntil(timestamp += 1000, new SteerPutEvent(SteerPutEvent.CMD_ON, -0.2));
    doUntil(timestamp += 500, new SteerPutEvent(SteerPutEvent.CMD_ON, 0.1));
  }
}
