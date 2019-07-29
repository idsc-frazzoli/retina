// code by jph
package ch.ethz.idsc.demo.jph.sys;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.demo.GokartLogFile;
import ch.ethz.idsc.retina.util.sys.AppCustomization;
import ch.ethz.idsc.retina.util.sys.WindowConfiguration;
import lcm.logging.LogPlayer;
import lcm.logging.LogPlayerConfig;

/* package */ enum GokartLcmLogPlayer {
  ;
  public static void main(String[] args) throws IOException {
    LogPlayerConfig logPlayerConfig = new LogPlayerConfig();
    File file = null;
    file = DatahakiLogFileLocator.file(GokartLogFile._20190627T143848_12dcbfa8);
    // file = new File("/media/datahaki/data/gokart/cuts4/20190309/20190309T160311_00", "post.lcm");
    // file = new File("/media/datahaki/data/gokart/cuts/20190401", "20190401T115537_411917b6.lcm.00");
    // file = new File("/media/datahaki/media/ethz/gokart/topic/trackid", "changingtrack.lcm");
    // file = new File("/media/datahaki/data/gokart/cuts/20190318/20190318T142605_08/post.lcm");
    // file = new File("/media/datahaki/data/gokart/vmu932/20190527/20190527T145700_00", "log.lcm");
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
