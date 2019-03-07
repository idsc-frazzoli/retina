// code by jph used by mh
package ch.ethz.idsc.demo.mh;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.gokart.gui.top.LocalViewLcmModule;
import ch.ethz.idsc.gokart.gui.top.PresenterLcmModule;
import ch.ethz.idsc.retina.util.sys.AppCustomization;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.retina.util.sys.WindowConfiguration;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import lcm.logging.LogPlayer;
import lcm.logging.LogPlayerConfig;

/* package */ enum GokartLcmLogPlayer {
  ;
  public static void main(String[] args) throws IOException {
    LogPlayerConfig logPlayerConfig = new LogPlayerConfig();
    File file;
    // file = DatahakiLogFileLocator.file(GokartLogFile._20181018T140542_1a649e65);
    // file = new File("/media/datahaki/media/ethz/gokart/topic/track_orange/20181008T183011_10/log.lcm");
    file = HomeDirectory.file("20190125T134537_e5eb6f95.lcm.00");
    logPlayerConfig.logFile = file.toString();
    logPlayerConfig.speed_numerator = 1;
    logPlayerConfig.speed_denominator = 1;
    LogPlayer logPlayer = LogPlayer.create(logPlayerConfig);
    WindowConfiguration windowConfiguration = //
        AppCustomization.load(GokartLcmLogPlayer.class, new WindowConfiguration());
    windowConfiguration.attach(GokartLcmLogPlayer.class, logPlayer.jFrame);
    logPlayer.jFrame.setLocation(100, 100);
    logPlayer.standalone();
    // GokartMappingModule gokartMappingModule = new GokartMappingModule();
    // gokartMappingModule.start();
    // ModuleAuto.INSTANCE.runOne(GyroOfflineLocalize.class);
    // ModuleAuto.INSTANCE.runOne(GlobalViewLcmModule.class);
    ModuleAuto.INSTANCE.runOne(PresenterLcmModule.class);
    ModuleAuto.INSTANCE.runOne(LocalViewLcmModule.class);
  }
}
