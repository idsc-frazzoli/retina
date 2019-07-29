// code by mg
package ch.ethz.idsc.demo.mg;

import java.io.IOException;

import ch.ethz.idsc.gokart.gui.SeyeDetailModule;
import ch.ethz.idsc.retina.app.slam.config.EventCamera;
import ch.ethz.idsc.retina.app.slam.config.SlamDvsConfig;
import lcm.logging.LogPlayer;
import lcm.logging.LogPlayerConfig;

// to test live version of SLAM algorithm
/* package */ enum GokartLcmLogPlayer {
  ;
  public static void main(String[] args) throws IOException {
    SlamDvsConfig.eventCamera = EventCamera.DAVIS;
    LogPlayerConfig cfg = new LogPlayerConfig();
    cfg.logFile = SlamDvsConfig.eventCamera.slamCoreConfig.dvsConfig.getLogFile().toString();
    LogPlayer.create(cfg);
    try {
      // DavisDetailModule.standalone();
      SeyeDetailModule.standalone();
      // DvsSlamBaseModule.standalone();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }
}
