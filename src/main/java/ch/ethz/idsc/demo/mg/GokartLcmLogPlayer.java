// code by mg
package ch.ethz.idsc.demo.mg;

import java.io.IOException;

import ch.ethz.idsc.demo.mg.slam.config.SlamCoreConfig;
import ch.ethz.idsc.demo.mg.slam.online.DavisSlamBaseModule;
import ch.ethz.idsc.gokart.gui.DavisDetailModule;
import lcm.logging.LogPlayer;
import lcm.logging.LogPlayerConfig;

// to test live version of SLAM algorithm
enum GokartLcmLogPlayer {
  ;
  public static void main(String[] args) throws IOException {
    LogPlayerConfig cfg = new LogPlayerConfig();
    cfg.logFile = SlamCoreConfig.GLOBAL.davisConfig.getLogFile().toString();
    LogPlayer.create(cfg);
    try {
      DavisDetailModule.standalone();
      // DavisSlamBaseModule.standalone();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
