// code by jph
package ch.ethz.idsc.retina.demo.jph;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.retina.demo.DubendorfHangarLog;
import ch.ethz.idsc.subare.util.UserHome;
import ch.ethz.idsc.tensor.RationalScalar;
import lcm.logging.LogPlayer;
import lcm.logging.LogPlayerConfig;

enum GokartLcmLogPlayer {
  ;
  public static final File LOG_ROOT = new File("/media/datahaki/media/ethz/gokartlogs");

  public static void main(String[] args) throws IOException {
    LogPlayerConfig cfg = new LogPlayerConfig();
    File file;
    file = new File("/media/datahaki/media/ethz/gokartlogs", "20180112T105400_9e1d3699.lcm.00");
    file = new File("/media/datahaki/media/ethz/gokartlogs", "20180112T113153_9e1d3699.lcm.00");
    file = DubendorfHangarLog._20180108T152648_5f742add.file(LOG_ROOT);
    file = new File( //
        "/home/datahaki/Projects/retina/src/test/resources/localization", //
        "vlp16.center.ray_autobox.rimo.get.lcm");
    file = new File("/home/datahaki/temp/20180108T162528_5f742add.lcm.00.extract");
    file = UserHome.file("temp/20171213T162832_brake5.lcm");
    file = DubendorfHangarLog._20180112T154355_9e1d3699.file(LOG_ROOT);
    cfg.logFile = file.toString();
    cfg.speed = RationalScalar.of(1, 1);
    LogPlayer.create(cfg);
  }
}
