// code by jph
package ch.ethz.idsc.demo.mg;

import java.io.IOException;

import ch.ethz.idsc.owl.bot.util.UserHome;
import lcm.logging.LogPlayer;
import lcm.logging.LogPlayerConfig;

enum GokartLcmLogPlayer {
  ;
  public static void main(String[] args) throws IOException {
    LogPlayerConfig cfg = new LogPlayerConfig();
    cfg.logFile = UserHome.file(LogfileLocations.DUBI4a).toString();
    LogPlayer.create(cfg);
  }
}
