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
    file = DatahakiLogFileLocator.file(GokartLogFile._20181003T155915_f6edefe8);
    // file = new File("/media/datahaki/media/ethz/gokart/topic/davis_extracted_logs/20180927T145943/log.lcm");
    // file = UserHome.file("20180927T145943_44599876Extracted1_Pose.lcm");
    cfg.logFile = file.toString();
    cfg.speed_numerator = 1;
    cfg.speed_denominator = 1;
    LogPlayer.create(cfg);
  }
}
