// code by mg
package ch.ethz.idsc.demo.mg.slam.offline;

import java.io.File;

import ch.ethz.idsc.demo.BoundedOfflineLogPlayer;
import ch.ethz.idsc.demo.mg.slam.config.SlamCoreConfig;
import ch.ethz.idsc.retina.util.math.Magnitude;

/** sets up the SLAM algorithm for offline processing of a log file */
/* package */ class SlamSetup {
  private final File logFile = SlamCoreConfig.GLOBAL.davisConfig.getLogFile();
  private final long logFileDuration = Magnitude.MICRO_SECOND.toLong(SlamCoreConfig.GLOBAL.davisConfig.logFileDuration);

  private void runAlgo() {
    OfflineSlamWrap offlineSlamWrap = new OfflineSlamWrap();
    try {
      BoundedOfflineLogPlayer.process(logFile, logFileDuration, offlineSlamWrap);
      offlineSlamWrap.stop();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public static void main(String[] args) {
    SlamSetup slamSetup = new SlamSetup();
    slamSetup.runAlgo();
  }
}
