// code by az
package ch.ethz.idsc.demo.az;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.subare.util.UserHome;
import lcm.logging.LogPlayer;
import lcm.logging.LogPlayerConfig;

enum GokartLcmLogPlayer {
  ;
  public static final File LOG_ROOT = new File("/home/ale/datasets/ValentinaST");

  public static void main(String[] args) throws IOException {
    LogPlayerConfig cfg = new LogPlayerConfig();
    File file;
    file = UserHome.file("/datasets/ValentinaST/log.lcm");
    cfg.logFile = file.toString();
    cfg.speed_numerator = 1;
    cfg.speed_denominator = 3;
    LogPlayer.create(cfg);
  }
}
