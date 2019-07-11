// code by jph
package ch.ethz.idsc.demo.mp;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.retina.util.sys.AppCustomization;
import ch.ethz.idsc.retina.util.sys.WindowConfiguration;
import lcm.logging.LogPlayer;
import lcm.logging.LogPlayerConfig;

/* package */ enum GokartLcmLogPlayer {
  ;
  public static void main(String[] args) throws IOException {
    LogPlayerConfig logPlayerConfig = new LogPlayerConfig();
    File file;
    // file = new File("/home/maximilien/Downloads/20190215T150920_380160a9.lcm.00");
    // file = new File("/home/maximilien/Downloads/20190401T101109_411917b6.lcm.00");
    // file = new File("/home/maximilien/Downloads/20190627T133639_12dcbfa8.lcm.00");
    file = new File("/home/maximilien/Downloads/20190708T114135_f3f46a8b.lcm.00");
    logPlayerConfig.logFile = file.toString();
    logPlayerConfig.speed_numerator = 1;
    logPlayerConfig.speed_denominator = 1;
    LogPlayer logPlayer = LogPlayer.create(logPlayerConfig);
    WindowConfiguration windowConfiguration = //
        AppCustomization.load(GokartLcmLogPlayer.class, new WindowConfiguration());
    windowConfiguration.attach(GokartLcmLogPlayer.class, logPlayer.jFrame);
    logPlayer.jFrame.setLocation(100, 100);
    logPlayer.standalone();
  }
}
