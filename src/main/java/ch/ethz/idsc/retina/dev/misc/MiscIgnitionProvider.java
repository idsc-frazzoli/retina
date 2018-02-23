// code by jph
package ch.ethz.idsc.retina.dev.misc;

import ch.ethz.idsc.gokart.core.AutoboxCalibrationProvider;

/** command sent to micro-autobox to acknowledge communication timeout
 * calibration procedure is mandatory to send at the beginning */
public class MiscIgnitionProvider extends AutoboxCalibrationProvider<MiscPutEvent> {
  public static final MiscIgnitionProvider INSTANCE = new MiscIgnitionProvider();
  private static final int DURATION_MS = 250;

  @Override // from AutoboxCalibrationProvider
  protected void protected_schedule() {
    MiscPutEvent miscPutEvent = new MiscPutEvent();
    miscPutEvent.resetConnection = 1;
    eventUntil(now_ms() + DURATION_MS, miscPutEvent);
  }
}
