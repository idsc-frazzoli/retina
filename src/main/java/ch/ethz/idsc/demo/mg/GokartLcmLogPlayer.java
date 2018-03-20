// code by jph
package ch.ethz.idsc.demo.mg;

import java.io.IOException;

import lcm.logging.LogPlayer;
import lcm.logging.LogPlayerConfig;

enum GokartLcmLogPlayer {
  ;
  public static void main(String[] args) throws IOException {
    LogPlayerConfig cfg = new LogPlayerConfig();
    cfg.logFile = LogfileLocations.DUBI1;
    LogPlayer.create(cfg);
  }
}
