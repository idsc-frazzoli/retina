// code by jph
package ch.ethz.idsc.demo.yt;

import java.io.File;
import java.io.IOException;

import lcm.logging.LogPlayer;
import lcm.logging.LogPlayerConfig;

enum GokartLcmLogPlayer {
  ;
  public static final File LOG_ROOT = new File("/media/datahaki/media/ethz/gokartlogs");

  public static void main(String[] args) throws IOException {
    LogPlayerConfig cfg = new LogPlayerConfig();
    File file;
    // file = new File("/media/datahaki/media/ethz/gokartlogs", "20180226T150533_ed1c7f0a.lcm.00");
    file = new File("/home/datahaki/interesting.lcm");
    cfg.logFile = file.toString();
    cfg.speed_numerator = 1;
    cfg.speed_denominator = 2;
    LogPlayer.create(cfg);
  }
}
