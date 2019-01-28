// code by jph used by mh
package ch.ethz.idsc.demo.mh;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.gokart.core.map.GokartTrackReconModule;
import ch.ethz.idsc.gokart.core.mpc.MPCKinematicDrivingModule;
import ch.ethz.idsc.gokart.gui.lab.TrackReconPanelModule;
import ch.ethz.idsc.gokart.gui.top.PresenterLcmModule;
import ch.ethz.idsc.gokart.offline.slam.GyroOfflineLocalize;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import lcm.logging.LogPlayer;
import lcm.logging.LogPlayerConfig;

/* package */ enum GokartLcmLogPlayerWithTrajectoryMapping {
  ;
  public static void main(String[] args) throws IOException {
    LogPlayerConfig cfg = new LogPlayerConfig();
    // File file;
    // file = DatahakiLogFileLocator.file(GokartLogFile._20181018T140542_1a649e65);
    // file = new File("/media/datahaki/media/ethz/gokart/topic/track_orange/20181008T183011_10/log.lcm");
    // file = UserHome.file("20181203T142514_70097ce1.lcm.00");
    File file = HomeDirectory.file("TireTrackDriving.lcm");
    cfg.logFile = file.toString();
    cfg.speed_numerator = 1;
    cfg.speed_denominator = 1;
    LogPlayer.create(cfg);
    // GokartMappingModule gokartMappingModule = new GokartMappingModule();
    // gokartMappingModule.start();
    ModuleAuto.INSTANCE.runOne(GyroOfflineLocalize.class);
    // ModuleAuto.INSTANCE.runOne(GlobalViewLcmModule.class);
    ModuleAuto.INSTANCE.runOne(GokartTrackReconModule.class);
    ModuleAuto.INSTANCE.runOne(PresenterLcmModule.class);
    ModuleAuto.INSTANCE.runOne(TrackReconPanelModule.class);
    ModuleAuto.INSTANCE.runOne(MPCKinematicDrivingModule.class);
  }
}
