// code by mg
package ch.ethz.idsc.demo.mg.slam.log;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.demo.mg.slam.SlamCoreContainer;
import ch.ethz.idsc.demo.mg.slam.SlamFileLocations;
import ch.ethz.idsc.demo.mg.slam.config.SlamCoreConfig;
import ch.ethz.idsc.demo.mg.slam.core.PeriodicSlamStep;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.retina.util.StartAndStoppable;

/** save CSV logs when testing the SLAM algorithm offline */
public class SlamLogCollection extends PeriodicSlamStep implements StartAndStoppable {
  private final GokartPoseInterface gokartLidarPose;
  private final String filename;
  private final List<double[]> logData;

  public SlamLogCollection(SlamCoreContainer slamCoreContainer, GokartPoseInterface gokartPoseInterface) {
    super(slamCoreContainer, SlamCoreConfig.GLOBAL.logCollectionUpdateRate);
    this.gokartLidarPose = gokartPoseInterface;
    filename = SlamCoreConfig.GLOBAL.davisConfig.logFilename();
    logData = new ArrayList<>();
  }

  @Override // from PeriodicSlamStep
  protected void periodicTask(int currentTimeStamp, int lastComputationTimeStamp) {
    SlamLogCollectionUtil.savePoseEstimates(currentTimeStamp, gokartLidarPose.getPose(), //
        slamCoreContainer.getPoseUnitless(), logData);
  }

  @Override // from StartAndStoppable
  public void start() {
    // ---
  }

  @Override // from StartAndStoppable
  public void stop() {
    CsvIO.saveToCSV(SlamFileLocations.OFFLINELOGS.inFolder(filename), logData);
    System.out.println("log data successfully saved");
  }
}
