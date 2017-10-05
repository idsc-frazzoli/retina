// code by jph
package ch.ethz.idsc.retina.demo.jph.lidar;

import java.io.IOException;

import ch.ethz.idsc.tensor.RationalScalar;
import lcm.logging.LogPlayer;
import lcm.logging.LogPlayerConfig;

enum LidarLcmLogPlayer {
  ;
  public static void main(String[] args) throws IOException {
    LogPlayerConfig cfg = new LogPlayerConfig();
    // cfg.logFile = LidarLcm.HDL32E_LOCALIZE2;
    cfg.logFile = LidarLcm.MARK8_LAB3;
    cfg.speed = RationalScalar.of(1, 8);
    LogPlayer.create(cfg);
  }
}
