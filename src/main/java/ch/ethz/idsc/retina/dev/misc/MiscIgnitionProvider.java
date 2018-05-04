// code by jph
package ch.ethz.idsc.retina.dev.misc;

import ch.ethz.idsc.gokart.core.AutoboxCalibrationProvider;
import ch.ethz.idsc.gokart.core.GetListener;

/** command sent to micro-autobox to acknowledge communication timeout
 * calibration procedure is mandatory to send at the beginning */
public class MiscIgnitionProvider extends AutoboxCalibrationProvider<MiscPutEvent> implements GetListener<MiscGetEvent> {
  /** instance is critical member of {@link MiscSocket} */
  public static final MiscIgnitionProvider INSTANCE = new MiscIgnitionProvider();
  // ---
  private static final int DURATION_MS = 250;
  private boolean isCommTimeout = false;

  @Override // from AutoboxCalibrationProvider
  protected void protected_schedule() {
    MiscPutEvent miscPutEvent = new MiscPutEvent();
    miscPutEvent.resetConnection = 1;
    eventUntil(now_ms() + DURATION_MS, miscPutEvent);
  }

  @Override // from GetListener
  public void getEvent(MiscGetEvent getEvent) {
    isCommTimeout = getEvent.isCommTimeout();
  }

  /** the application layer is discouraged to {@link #schedule()}
   * the calibration procedure unless {@link #isCommTimeout()} returns true
   * 
   * @return true if comm reset is required */
  public boolean isCommTimeout() {
    return isCommTimeout;
  }
}
