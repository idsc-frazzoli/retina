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
    
    // file = new File("/media/datahaki/media/ethz/gokart/topic/localization/20181211T153939_3/log.lcm");
    // file = new File("/media/datahaki/media/ethz/gokart/topic/trackid", "changingtrack.lcm");
    
    file = new File("/home/maximilien/Downloads/20190215T150920_380160a9.lcm.00");
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
