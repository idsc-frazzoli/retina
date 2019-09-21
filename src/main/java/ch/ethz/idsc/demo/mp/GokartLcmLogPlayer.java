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
    // file = new File("/home/maximilien/Downloads/20190708T114135_f3f46a8b.lcm.00");
    // file = new File("/home/maximilien/Downloads/20190711T160210_908ca07d.lcm.00");
    // file = new File("/home/maximilien/Downloads/20190921T112425_fa3ec462.lcm.00");
    // file = new File("/home/maximilien/Documents/sp/logs/20190921/20190921T1124_00/log.lcm");
    // file = new File("/home/maximilien/Downloads/20190921T142531_fa3ec462.lcm.00");
    // file = new File("/home/maximilien/Documents/sp/logs/20190921/20190921T142531_00/log.lcm");
    // file = new File("/home/maximilien/Downloads/20190921T175315_b27ad38d.lcm.00");
    file = new File("/home/maximilien/Documents/sp/logs/20190921/20190921T175315_00/log.lcm");

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
