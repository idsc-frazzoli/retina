// code by jph
package ch.ethz.idsc.retina.demo.jph.davis;

import java.io.IOException;

import ch.ethz.idsc.tensor.RationalScalar;
import lcm.logging.LogPlayer;
import lcm.logging.LogPlayerConfig;

enum DavisLcmLogPlayer {
  ;
  public static void main(String[] args) throws IOException {
    LogPlayerConfig cfg = new LogPlayerConfig();
    cfg.logFile = DavisRecordings.ETH4;
    cfg.speed = RationalScalar.of(1, 4);
    LogPlayer.create(cfg);
  }
}
