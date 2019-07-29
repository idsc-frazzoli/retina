// code by mg
package ch.ethz.idsc.retina.app.slam.offline;

import java.io.File;

import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.retina.app.slam.config.SlamDvsConfig;

/** sets up the SLAM algorithm for offline processing of a log file */
/* package */ class SlamSetup {
  private final File logFile = SlamDvsConfig.eventCamera.slamCoreConfig.dvsConfig.getLogFile();

  private void runAlgo() {
    OfflineSlamWrap offlineSlamWrap = new OfflineSlamWrap();
    try {
      OfflineLogPlayer.process(logFile, offlineSlamWrap);
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
