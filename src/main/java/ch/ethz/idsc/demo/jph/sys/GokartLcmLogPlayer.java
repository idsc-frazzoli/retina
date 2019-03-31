// code by jph
package ch.ethz.idsc.demo.jph.sys;

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
    // file = DatahakiLogFileLocator.file(GokartLogFile._20190311T173809_da0bb9b9);
    file = new File("/media/datahaki/data/gokart/cuts/20190311/20190311T173809_02/log.lcm");
    // file = new File("/media/datahaki/media/ethz/gokart/topic/trackid", "changingtrack.lcm");
    // file = new File("/media/datahaki/data/gokart/cuts/20190318/20190318T142605_08/post.lcm");
    logPlayerConfig.logFile = file.toString();
    logPlayerConfig.speed_numerator = 1;
    logPlayerConfig.speed_denominator = 8;
    LogPlayer logPlayer = LogPlayer.create(logPlayerConfig);
    WindowConfiguration windowConfiguration = //
        AppCustomization.load(GokartLcmLogPlayer.class, new WindowConfiguration());
    windowConfiguration.attach(GokartLcmLogPlayer.class, logPlayer.jFrame);
    logPlayer.jFrame.setLocation(100, 100);
    logPlayer.standalone();
  }
}
