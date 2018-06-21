// code by jph
package ch.ethz.idsc.demo.jph.sys;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.demo.GokartLogFile;
import ch.ethz.idsc.subare.util.UserHome;
import lcm.logging.LogPlayer;
import lcm.logging.LogPlayerConfig;

/* package */ enum GokartLcmLogPlayer {
  ;
  public static void main(String[] args) throws IOException {
    LogPlayerConfig cfg = new LogPlayerConfig();
    File file;
    file = UserHome.file("gokart/manual/20180427T105421_circle/log.lcm");
    file = DatahakiLogFileLocator.file(GokartLogFile._20180430T104113_a5291af9);
    file = DatahakiLogFileLocator.file(GokartLogFile._20180604T150508_15e65bba);
    file = UserHome.file("gokart/pedestrian/20180604T150508/log.lcm");
    file = DatahakiLogFileLocator.file(GokartLogFile._20180607T140443_e9d47681);
    cfg.logFile = file.toString();
    cfg.speed_numerator = 1;
    cfg.speed_denominator = 2;
    LogPlayer.create(cfg);
  }
}
