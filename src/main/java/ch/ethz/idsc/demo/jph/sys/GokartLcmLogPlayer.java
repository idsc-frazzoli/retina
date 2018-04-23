// code by jph
package ch.ethz.idsc.demo.jph.sys;

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
    file = UserHome.file("gokart/pursuit/20180112T154355/log.lcm");
    file = UserHome.file("gokart/manual/20180226T150533/log.lcm");
    file = UserHome.file("gokartlogs/20180419/20180419T150253_7373f83e.lcm.00");
    file = new File("/media/datahaki/media/ethz/gokartlogs", "20180226T150533_ed1c7f0a.lcm.00");
    file = new File("/media/datahaki/backup/gokartlogs/20180418", "20180418T132333_bca165ae.lcm.00");
    file = UserHome.file("gokart/pursuit/20180419T124700/log.lcm");
    file = UserHome.file("20180412T163855_7e5b46c2_predestrians.lcm");
    file = DubendorfHangarLog._20180419T124700_7373f83e.file(LOG_ROOT);
    file = UserHome.file("20180419T124700_7373f83e_fast.lcm");
    cfg.logFile = file.toString();
    cfg.speed_numerator = 1;
    cfg.speed_denominator = 2;
    LogPlayer.create(cfg);
  }
}
