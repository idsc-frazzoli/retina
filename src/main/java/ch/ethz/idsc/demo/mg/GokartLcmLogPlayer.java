// code by jph
package ch.ethz.idsc.demo.mg;

import java.io.IOException;

import ch.ethz.idsc.gokart.gui.DavisDetailModule;
import lcm.logging.LogPlayer;
import lcm.logging.LogPlayerConfig;

enum GokartLcmLogPlayer {
  ;
  public static void main(String[] args) throws IOException {
    LogPlayerConfig cfg = new LogPlayerConfig();
    cfg.logFile = LogfileLocations.DUBI8a;
    LogPlayer.create(cfg);
    try {
      DavisDetailModule.standalone();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
