// code by jph
package ch.ethz.idsc.demo.mg;

import ch.ethz.idsc.gokart.gui.DavisDetailModule;
import lcm.logging.LogPlayer;
import lcm.logging.LogPlayerConfig;

enum GokartLcmLogPlayer {
  ;
  public static void main(String[] args) throws Exception {
    LogPlayerConfig cfg = new LogPlayerConfig();
    DavisDetailModule davis = new DavisDetailModule();
    cfg.logFile = LogfileLocations.DUBI4;
    LogPlayer.create(cfg);
    DavisDetailModule.standalone();
  }
}
