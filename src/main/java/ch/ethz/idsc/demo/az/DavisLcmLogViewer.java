// code by jph
package ch.ethz.idsc.demo.az;

import java.io.IOException;

import lcm.logging.LogPlayer;
import lcm.logging.LogPlayerConfig;

/* package */ enum DavisLcmLogViewer {
  ;
  public static void main(String[] args) throws IOException {
    LogPlayerConfig cfg = new LogPlayerConfig();
    cfg.logFile = "/home/ale/datasets/zuriscapes/rec_TRAINlong_lcm/lcmlog-2017-11-08.09";
    cfg.speed_denominator = 4;
    LogPlayer.create(cfg);
  }
}
