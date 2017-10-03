// code by ej
package ch.ethz.idsc.retina.dev.steer;

import ch.ethz.idsc.retina.dev.zhkart.AutoboxCalibrationProvider;
import ch.ethz.idsc.retina.dev.zhkart.TimedPutEvent;

public class SteerCalibrationProvider extends AutoboxCalibrationProvider<SteerPutEvent> {
  public static final SteerCalibrationProvider INSTANCE = new SteerCalibrationProvider();

  private SteerCalibrationProvider() {
  }

  public void schedule() {
    long timestamp = System.currentTimeMillis();
    // TODO this torque is magic constant, change it
    add(new TimedPutEvent<>(timestamp += 1000, new SteerPutEvent(SteerPutEvent.CMD_ON, 0.1)));
    add(new TimedPutEvent<>(timestamp += 1000, new SteerPutEvent(SteerPutEvent.CMD_ON, 0.2)));
    add(new TimedPutEvent<>(timestamp += 2000, new SteerPutEvent(SteerPutEvent.CMD_ON, -0.1)));
    add(new TimedPutEvent<>(timestamp += 1000, new SteerPutEvent(SteerPutEvent.CMD_ON, -0.2)));
    // TODO EJDH briefly turn into other direction to visually indicate that done
  }
}
