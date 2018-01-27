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
    File file;
    file = new File("/media/datahaki/media/ethz/gokartlogs", "20180112T105400_9e1d3699.lcm.00");
    file = new File("/media/datahaki/media/ethz/gokartlogs", "20180112T113153_9e1d3699.lcm.00");
    // file = UserHome.file("vlp16.lcm");
    cfg.logFile = file.toString();
    cfg.speed = RationalScalar.of(1, 1);
    LogPlayer.create(cfg);
  }
}
