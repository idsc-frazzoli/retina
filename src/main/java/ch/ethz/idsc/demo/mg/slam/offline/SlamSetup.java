// code by mg
package ch.ethz.idsc.demo.mg.slam.offline;

import java.io.File;

import ch.ethz.idsc.demo.BoundedOfflineLogPlayer;
import ch.ethz.idsc.demo.mg.slam.config.SlamCoreConfig;
import ch.ethz.idsc.retina.util.math.Magnitude;

/** sets up the SLAM algorithm for offline processing of a log file */
/* package */ class SlamSetup {
  private final SlamCoreConfig slamConfig;
  private final File logFile;
  private final long logFileDuration;

  SlamSetup(SlamCoreConfig slamConfig) {
    this.slamConfig = slamConfig;
    logFile = slamConfig.davisConfig.getLogFile();
    logFileDuration = Magnitude.MICRO_SECOND.toLong(slamConfig.davisConfig.logFileDuration);
  }

  private void runAlgo() {
    OfflineSlamWrap offlineSlamWrap = new OfflineSlamWrap(slamConfig);
    try {
      BoundedOfflineLogPlayer.process(logFile, logFileDuration, offlineSlamWrap);
      offlineSlamWrap.stop();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public static void main(String[] args) {
    SlamSetup slamSetup = new SlamSetup(new SlamCoreConfig());
    slamSetup.runAlgo();
  }
}
