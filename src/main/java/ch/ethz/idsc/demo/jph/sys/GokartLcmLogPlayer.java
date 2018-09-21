// code by jph
package ch.ethz.idsc.demo.jph.sys;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.demo.GokartLogFile;
import lcm.logging.LogPlayer;
import lcm.logging.LogPlayerConfig;

/* package */ enum GokartLcmLogPlayer {
  ;
  public static void main(String[] args) throws IOException {
    LogPlayerConfig cfg = new LogPlayerConfig();
    File file;
    file = DatahakiLogFileLocator.file(GokartLogFile._20180913T183146_34b3470d);
    // file = new File("/media/datahaki/media/ethz/gokart/topic/mapping/20180827T155655_1/log.lcm");
    // file = UserHome.file("export_red/copy20180820T143852_1.lcm");
    cfg.logFile = file.toString();
    cfg.speed_numerator = 1;
    cfg.speed_denominator = 1;
    LogPlayer.create(cfg);
  }
}
