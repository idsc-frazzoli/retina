// code by jph
package ch.ethz.idsc.demo.jph.davis;

import java.io.IOException;

import lcm.logging.LogPlayer;
import lcm.logging.LogPlayerConfig;

enum DavisLcmLogPlayer {
  ;
  public static void main(String[] args) throws IOException {
    LogPlayerConfig cfg = new LogPlayerConfig();
    cfg.logFile = DavisRecordings.ETH4;
    cfg.speed_denominator = 4;
    LogPlayer.create(cfg);
  }
}
