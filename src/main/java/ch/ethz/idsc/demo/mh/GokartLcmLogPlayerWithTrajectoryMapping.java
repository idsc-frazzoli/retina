// code by jph used by mh
package ch.ethz.idsc.demo.mh;

import java.io.File;

import ch.ethz.idsc.gokart.core.mpc.MPCDrivingKinematicModule;
import ch.ethz.idsc.gokart.core.track.TrackReconModule;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import lcm.logging.LogPlayer;
import lcm.logging.LogPlayerConfig;

/* package */ enum GokartLcmLogPlayerWithTrajectoryMapping {
  ;
  public static void main(String[] args) throws Exception {
    LogPlayerConfig cfg = new LogPlayerConfig();
    // File file;
    // file = UserHome.file("20181203T142514_70097ce1.lcm.00");
    File file = HomeDirectory.file("TireTrackDriving.lcm");
    cfg.logFile = file.toString();
    cfg.speed_numerator = 1;
    cfg.speed_denominator = 1;
    LogPlayer.create(cfg);
    // GokartMappingModule gokartMappingModule = new GokartMappingModule();
    // gokartMappingModule.start();
    // ModuleAuto.INSTANCE.runOne(GyroOfflineLocalize.class);
    // ModuleAuto.INSTANCE.runOne(GlobalViewLcmModule.class);
    ModuleAuto.INSTANCE.runOne(TrackReconModule.class);
    // ModuleAuto.INSTANCE.runOne(PresenterLcmModule.class);
    ModuleAuto.INSTANCE.runOne(MPCDrivingKinematicModule.class);
  }
}
