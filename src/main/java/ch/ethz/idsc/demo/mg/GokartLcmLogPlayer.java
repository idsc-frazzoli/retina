// code by mg
package ch.ethz.idsc.demo.mg;

import java.io.IOException;

import ch.ethz.idsc.demo.mg.slam.config.SlamCoreConfig;
import ch.ethz.idsc.demo.mg.slam.online.DvsSlamBaseModule;
import ch.ethz.idsc.gokart.gui.DavisDetailModule;
import ch.ethz.idsc.gokart.gui.SeyeDetailModule;
import lcm.logging.LogPlayer;
import lcm.logging.LogPlayerConfig;

// to test live version of SLAM algorithm
enum GokartLcmLogPlayer {
  ;
  public static void main(String[] args) throws IOException {
    LogPlayerConfig cfg = new LogPlayerConfig();
    cfg.logFile = SlamCoreConfig.GLOBAL.dvsConfig.getLogFile().toString();
    LogPlayer.create(cfg);
    try {
      // DavisDetailModule.standalone();
      SeyeDetailModule.standalone();
      // DvsSlamBaseModule.standalone();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
