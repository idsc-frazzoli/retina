// code by jph
package ch.ethz.idsc.retina.app.area;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.tensor.io.HomeDirectory;
import lcm.logging.LogPlayer;
import lcm.logging.LogPlayerConfig;

/* package */ enum GokartLcmLogPlayer {
  ;
  public static void main(String[] args) throws IOException {
    LogPlayerConfig cfg = new LogPlayerConfig();
    File file;
    file = HomeDirectory.file("gokart/pursuit/20180112T154355/log.lcm");
    file = HomeDirectory.file("gokart/pursuit/20180108T165210/log.lcm");
    file = HomeDirectory.file("Desktop/ETHZ/2_MA2/0_SemesterProject/log.lcm");
    file = HomeDirectory.file("Desktop/ETHZ/log/pedestrian/20180412T163109/log.lcm");
    file = HomeDirectory.file("Desktop/ETHZ/log/pedestrian/20180412T163855/log.lcm");
    // file = UserHome.file("Desktop/20180604T120216_a2e94332.lcm.00");
    cfg.logFile = file.toString();
    cfg.speed_numerator = 1;
    cfg.speed_denominator = 2;
    LogPlayer.create(cfg);
  }
}