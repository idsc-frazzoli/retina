// code by nisaak, rvmoos, and jph
package ch.ethz.idsc.retina.dev.linmot;

import ch.ethz.idsc.retina.dev.zhkart.AutoboxCalibrationProvider;

public class LinmotCalibrationProvider extends AutoboxCalibrationProvider<LinmotPutEvent> {
  public static final LinmotCalibrationProvider INSTANCE = new LinmotCalibrationProvider();

  private LinmotCalibrationProvider() {
  }

  public void schedule() {
    long timestamp = System.currentTimeMillis();
    doUntil(timestamp += 200, new LinmotPutEvent( //
        LinmotPutHelper.CMD_ERR_ACK, LinmotPutHelper.MC_ZEROS));
    doUntil(timestamp += 200, LinmotPutHelper.OFF_MODE_EVENT);
    doUntil(timestamp += 4000, new LinmotPutEvent( //
        LinmotPutHelper.CMD_HOME, LinmotPutHelper.MC_ZEROS));
    doUntil(timestamp += 200, new LinmotPutEvent( //
        LinmotPutHelper.CMD_OPERATION, LinmotPutHelper.MC_ZEROS));
    doUntil(timestamp += 200, new LinmotPutEvent( //
        LinmotPutHelper.CMD_OPERATION, LinmotPutHelper.MC_POSITION));
  }
}
