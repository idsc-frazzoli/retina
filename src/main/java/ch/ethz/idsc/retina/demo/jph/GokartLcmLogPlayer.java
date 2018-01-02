// code by jph
package ch.ethz.idsc.retina.demo.jph;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.tensor.RationalScalar;
import lcm.logging.LogPlayer;
import lcm.logging.LogPlayerConfig;

enum GokartLcmLogPlayer {
  ;
  public static void main(String[] args) throws IOException {
    LogPlayerConfig cfg = new LogPlayerConfig();
    // cfg.logFile = "/media/datahaki/media/ethz/lcmlog/20171003T173555_db6268ca.lcm.00";
    // cfg.logFile = "/media/datahaki/media/ethz/lcmlog/20171030T120648_2c52725e.lcm.00_videodemo2";
    // cfg.logFile = "/media/datahaki/media/ethz/lcmlog/20171107T151536_9639c7f9.lcm.00_test_labday";
    // cfg.logFile = "/media/datahaki/media/ethz/lcmlog/20171116T151706_0b744a74.lcm.00_noah_dvs_lidar";
    // cfg.logFile = "/media/datahaki/media/ethz/lcmlog/20171207T134930_59f9bc78.lcm.00";
    // cfg.logFile = "/home/datahaki/20171207T134930_59f9bc78.lcm.00_part1";
    // cfg.logFile = "/media/datahaki/backup/gokartlogs/20171207/20171207T105632_59f9bc78.lcm.00";
    // cfg.logFile = "/home/datahaki/20171207T105632_59f9bc78.lcm.00_part1";
    File file = new File("/home/datahaki", "20171213T164051_55710a6b.lcm.00");
    // File file = new File("/home/datahaki", "20171218T130515_4794c081.lcm.00");
    cfg.logFile = file.toString();
    cfg.speed = RationalScalar.of(1, 1);
    LogPlayer.create(cfg);
  }
}
