// code by jph
package ch.ethz.idsc.demo.jph.lidar;

import java.io.IOException;

import lcm.logging.LogPlayer;
import lcm.logging.LogPlayerConfig;

/* package */ enum LidarLcmLogPlayer {
  ;
  public static void main(String[] args) throws IOException {
    LogPlayerConfig logPlayerConfig = new LogPlayerConfig();
    // cfg.logFile = LidarLcm.HDL32E_LOCALIZE2;
    logPlayerConfig.logFile = LidarLcm.MARK8_LAB3;
    logPlayerConfig.speed_denominator = 8;
    LogPlayer.create(logPlayerConfig);
  }
}
