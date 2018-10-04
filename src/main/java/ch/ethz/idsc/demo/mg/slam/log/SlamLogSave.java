// code by mg
package ch.ethz.idsc.demo.mg.slam.log;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.demo.mg.slam.SlamCoreContainer;
import ch.ethz.idsc.demo.mg.slam.SlamFileLocations;
import ch.ethz.idsc.demo.mg.slam.SlamPrcContainer;
import ch.ethz.idsc.demo.mg.slam.config.SlamCoreConfig;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;

/* package */ class SlamLogSave {
  private final SlamEventCounter slamEventCounter;
  private final SlamCoreContainer slamCoreContainer;
  private final SlamPrcContainer slamPrcContainer;
  private final GokartPoseInterface gokartLidarPose;
  private final String filename;
  private final List<double[]> logData;

  SlamLogSave(SlamCoreContainer slamCoreContainer, SlamPrcContainer slamPrcContainer, //
      GokartPoseInterface gokartPoseInterface, SlamEventCounter slamEventCounter) {
    this.slamEventCounter = slamEventCounter;
    this.slamCoreContainer = slamCoreContainer;
    this.slamPrcContainer = slamPrcContainer;
    this.gokartLidarPose = gokartPoseInterface;
    filename = SlamCoreConfig.GLOBAL.davisConfig.logFilename();
    logData = new ArrayList<>();
  }

  protected void logSaveTask(int currentTimeStamp) {
    // SlamLogCollectionUtil.savePoseEstimates(currentTimeStamp, gokartLidarPose.getPose(), //
    // slamCoreContainer.getPoseUnitless(), logData);
    SlamLogCollectionUtil.saveProcessedEventCount(currentTimeStamp, slamEventCounter.getProcessedEventCount(), //
        slamEventCounter.getRawEventCount(), logData);
    // Tensor gokartWaypoints = slamPrcContainer.getGokartWaypoints();
    // double xDistance = gokartWaypoints.get(gokartWaypoints.length() - 1).Get(0).number().doubleValue();
    // SlamLogCollectionUtil.saveWaypointDistance(currentTimeStamp, xDistance, logData);
  }

  public void stop() {
    CsvIO.saveToCSV(SlamFileLocations.OFFLINELOGS.inFolder(filename), logData);
    System.out.println("log data successfully saved");
  }
}
