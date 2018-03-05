// code by jph
package ch.ethz.idsc.demo.gz;

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
    file = new File("/home/datahaki/temp/20180108T162528_5f742add.lcm.00.extract");
    file = new File("/home/datahaki/gokart/localquick/20171213T162832_brake6/log.lcm");
    file = UserHome.file("temp/20180108T165210_manual.lcm");
    file = UserHome.file("gokart/twist/20180108T165210_4/log.lcm");
    file = UserHome.file("gokart/short/20180108T165210_1/log.lcm");
    // /home/datahaki/gokart/pursuit/20180108T165210
    file = UserHome.file("gokart/pursuit/20180112T154355/log.lcm");
    file = DubendorfHangarLog._20180108T154035_5f742add.file(LOG_ROOT);
    file = UserHome.file("gokart/manual/20180108T154035/log.lcm");
    file = UserHome.file("Downloads/log.lcm");
    cfg.logFile = file.toString();
    cfg.speed_numerator = 1;
    cfg.speed_denominator = 2;
    LogPlayer.create(cfg);
  }
}
