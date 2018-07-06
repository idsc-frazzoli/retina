// code by jph
package ch.ethz.idsc.demo.gz;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.demo.GokartLogFile;
import ch.ethz.idsc.subare.util.UserHome;
import lcm.logging.LogPlayer;
import lcm.logging.LogPlayerConfig;

enum GokartLcmLogPlayer {
  ;
  public static void main(String[] args) throws IOException {
    LogPlayerConfig cfg = new LogPlayerConfig();
    File file;
    file = new File( //
        "/home/datahaki/Projects/retina/src/test/resources/localization", //
        "vlp16.center.ray_autobox.rimo.get.lcm");
    file = UserHome.file("temp/20180108T165210_manual.lcm");
    file = UserHome.file("gokart/twist/20180108T165210_4/log.lcm");
    file = UserHome.file("gokart/short/20180108T165210_1/log.lcm");
    // /home/datahaki/gokart/pursuit/20180108T165210
    file = UserHome.file("gokart/pursuit/20180112T154355/log.lcm");
    file = UserHome.file("gokart/manual/20180108T154035/log.lcm");
    file = UserHome.file("Downloads/logs/log.lcm");
    file = GioeleLogFileLocator.file(GokartLogFile._20180509T120343_8d5acc24);
    cfg.logFile = file.toString();
    cfg.speed_numerator = 1;
    cfg.speed_denominator = 2;
    LogPlayer.create(cfg);
  }
}
