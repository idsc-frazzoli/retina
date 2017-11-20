// code by jph
package ch.ethz.idsc.retina.dev.misc;

import ch.ethz.idsc.retina.dev.zhkart.AutoboxCalibrationProvider;

public class MiscIgnitionProvider extends AutoboxCalibrationProvider<MiscPutEvent> {
  public static final MiscIgnitionProvider INSTANCE = new MiscIgnitionProvider();

  @Override
  protected void protected_schedule() {
    long timestamp = now();
    {
      MiscPutEvent miscPutEvent = new MiscPutEvent();
      miscPutEvent.resetConnection = 1;
      eventUntil(timestamp += 250, miscPutEvent);
    }
  }
}
