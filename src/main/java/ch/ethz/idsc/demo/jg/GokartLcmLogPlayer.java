// code by jph
package ch.ethz.idsc.demo.jg;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import ch.ethz.idsc.retina.util.sys.AppCustomization;
import ch.ethz.idsc.retina.util.sys.WindowConfiguration;
import lcm.logging.LogPlayer;
import lcm.logging.LogPlayerConfig;

/* package */ enum GokartLcmLogPlayer {
  ;
  public static void main(String[] args) throws IOException {
    LogPlayerConfig logPlayerConfig = new LogPlayerConfig();
    Optional<File> file = FileHelper.open(args);
    if (file.isPresent()) {
      logPlayerConfig.logFile = file.get().toString();
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
}
