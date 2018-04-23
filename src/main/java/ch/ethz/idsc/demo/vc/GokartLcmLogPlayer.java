// code by jph
package ch.ethz.idsc.demo.vc;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.demo.DubendorfHangarLog;
import ch.ethz.idsc.subare.util.UserHome;
import lcm.logging.LogPlayer;
import lcm.logging.LogPlayerConfig;

enum GokartLcmLogPlayer {
  ;
  public static final File LOG_ROOT = new File("/media/datahaki/media/ethz/gokartlogs");

  public static void main(String[] args) throws IOException {
    LogPlayerConfig cfg = new LogPlayerConfig();
    File file;
    file = new File( //
        "/home/datahaki/Projects/retina/src/test/resources/localization", //
        "vlp16.center.ray_autobox.rimo.get.lcm");
    file = UserHome.file("gokart/pursuit/20180112T154355/log.lcm");
    file = DubendorfHangarLog._20180108T154035_5f742add.file(LOG_ROOT);
    file = UserHome.file("gokart/pursuit/20180108T165210/log.lcm");
    file = new File("/media/datahaki/media/ethz/gokartlogs", "20180226T150533_ed1c7f0a.lcm.00");
    file = UserHome.file("/Desktop/ETHZ/2_MA2/0_SemesterProject/log.lcm");
    file = UserHome.file("/Desktop/ETHZ/log/20180412T163855_7e5b46c2.lcm.00");
    // file = UserHome.file("gokartlogs/20180308/20180308T151702_45b9eed8.lcm.00");
    cfg.logFile = file.toString();
    cfg.speed_numerator = 1;
    cfg.speed_denominator = 2;
    LogPlayer.create(cfg);
  }
}