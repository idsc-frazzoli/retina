// code by nisaak, rvmoos, and jph
package ch.ethz.idsc.gokart.dev.linmot;

import ch.ethz.idsc.gokart.core.AutoboxCalibrationProvider;
import ch.ethz.idsc.gokart.core.GetListener;

/** the procedure re-initializes linmot brake from any initial condition.
 * the procedure leaves the linmot in positioning mode so that
 * position commands are executed */
public class LinmotCalibrationProvider extends AutoboxCalibrationProvider<LinmotPutEvent> //
    implements GetListener<LinmotGetEvent> {
  /** instance is a critical member of {@link LinmotSocket} */
  public static final LinmotCalibrationProvider INSTANCE = new LinmotCalibrationProvider();
  // ---
  /** by default no calibration is necessary */
  private boolean isOperational = true;

  private LinmotCalibrationProvider() {
  }

  @Override // from AutoboxCalibrationProvider
  protected void protected_schedule() {
    long timestamp = now_ms();
    eventUntil(timestamp += 200, //
        () -> LinmotPutOperation.INSTANCE.configuration(LinmotPutHelper.CMD_ERR_ACK, LinmotPutHelper.MC_ZEROS));
    // ---
    eventUntil(timestamp += 200, () -> LinmotPutOperation.INSTANCE.offMode());
    // ---
    eventUntil(timestamp += 4000, //
        () -> LinmotPutOperation.INSTANCE.configuration(LinmotPutHelper.CMD_HOME, LinmotPutHelper.MC_ZEROS));
    // ---
    eventUntil(timestamp += 200, //
        () -> LinmotPutOperation.INSTANCE.configuration(LinmotPutHelper.CMD_OPERATION, LinmotPutHelper.MC_ZEROS));
    // ---
    /** the last "position" command with all ratings == 0 is required */
    eventUntil(timestamp += 200, //
        () -> LinmotPutOperation.INSTANCE.configuration(LinmotPutHelper.CMD_OPERATION, LinmotPutHelper.MC_POSITION));
  }

  @Override // from AutoboxCalibrationProvider
  protected boolean hintScheduleRequired() {
    return !isOperational;
  }

  @Override // from GetListener
  public void getEvent(LinmotGetEvent linmotGetEvent) {
    isOperational = linmotGetEvent.isOperational();
  }
}
