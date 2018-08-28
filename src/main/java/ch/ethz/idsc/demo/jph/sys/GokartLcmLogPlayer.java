// code by jph
package ch.ethz.idsc.demo.jph.sys;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.demo.GokartLogFile;
import ch.ethz.idsc.owl.bot.util.UserHome;
import lcm.logging.LogPlayer;
import lcm.logging.LogPlayerConfig;

/* package */ enum GokartLcmLogPlayer {
  ;
  public static void main(String[] args) throws IOException {
    LogPlayerConfig cfg = new LogPlayerConfig();
    File file;
    file = DatahakiLogFileLocator.file(GokartLogFile._20180820T165637_568f9954);
    file = new File("/media/datahaki/media/ethz/gokart/topic/track_red/20180820T165637_5/log.lcm");
    file = UserHome.file("export_red/copy20180820T143852_1.lcm");
    cfg.logFile = file.toString();
    cfg.speed_numerator = 1;
    cfg.speed_denominator = 8;
    LogPlayer.create(cfg);
  }
}
