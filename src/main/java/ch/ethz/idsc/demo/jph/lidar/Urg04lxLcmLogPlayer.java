// code by jph
package ch.ethz.idsc.demo.jph.lidar;

import java.io.IOException;

import lcm.logging.LogPlayer;
import lcm.logging.LogPlayerConfig;

enum Urg04lxLcmLogPlayer {
  ;
  public static void main(String[] args) throws IOException {
    LogPlayerConfig cfg = new LogPlayerConfig();
    cfg.logFile = Urg.LCMLOG02.file.toString();
    cfg.speed_denominator = 4;
    LogPlayer.create(cfg);
  }
}
