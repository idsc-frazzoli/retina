// code by nisaak, rvmoos, and jph
package ch.ethz.idsc.retina.dev.linmot;

import ch.ethz.idsc.gokart.core.AutoboxCalibrationProvider;

/** the procedure re-initializes linmot brake from any initial condition.
 * the procedure leaves the linmot in positioning mode so that
 * position commands are executed */
public class LinmotCalibrationProvider extends AutoboxCalibrationProvider<LinmotPutEvent> {
  public static final LinmotCalibrationProvider INSTANCE = new LinmotCalibrationProvider();
  // ---

  private LinmotCalibrationProvider() {
  }

  @Override // from AutoboxCalibrationProvider
  protected void protected_schedule() {
    long timestamp = now_ms();
    eventUntil(timestamp += 200, //
        LinmotPutEvent.configuration(LinmotPutHelper.CMD_ERR_ACK, LinmotPutHelper.MC_ZEROS));
    // ---
    eventUntil(timestamp += 200, LinmotPutHelper.OFF_MODE_EVENT);
    // ---
    eventUntil(timestamp += 4000, //
        LinmotPutEvent.configuration(LinmotPutHelper.CMD_HOME, LinmotPutHelper.MC_ZEROS));
    // ---
    eventUntil(timestamp += 200, //
        LinmotPutEvent.configuration(LinmotPutHelper.CMD_OPERATION, LinmotPutHelper.MC_ZEROS));
    // ---
    /** the last "position" command with all ratings == 0 is required */
    eventUntil(timestamp += 200, //
        LinmotPutEvent.configuration(LinmotPutHelper.CMD_OPERATION, LinmotPutHelper.MC_POSITION));
  }
}
