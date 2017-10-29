// code by jph
package ch.ethz.idsc.retina.demo.jph;

import java.io.IOException;

import ch.ethz.idsc.tensor.RationalScalar;
import lcm.logging.LogPlayer;
import lcm.logging.LogPlayerConfig;

enum GokartLcmLogPlayer {
  ;
  public static void main(String[] args) throws IOException {
    LogPlayerConfig cfg = new LogPlayerConfig();
    cfg.logFile = "/media/datahaki/media/ethz/lcmlog/20171003T173555_db6268ca.lcm.00";
    cfg.speed = RationalScalar.of(1, 4);
    LogPlayer.create(cfg);
  }
}
