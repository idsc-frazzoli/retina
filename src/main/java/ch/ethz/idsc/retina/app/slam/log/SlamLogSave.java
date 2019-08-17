// code by mg
package ch.ethz.idsc.retina.app.slam.log;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.retina.app.slam.SlamCoreContainer;
import ch.ethz.idsc.retina.app.slam.SlamFileLocations;
import ch.ethz.idsc.retina.app.slam.SlamPrcContainer;
import ch.ethz.idsc.retina.app.slam.config.SlamDvsConfig;
import ch.ethz.idsc.retina.util.pose.PoseInterface;

@SuppressWarnings("unused")
/* package */ class SlamLogSave {
  private final SlamEventCounter slamEventCounter;
  private final SlamCoreContainer slamCoreContainer;
  private final SlamPrcContainer slamPrcContainer;
  private final PoseInterface poseInterface;
  private final String filename;
  private final List<double[]> logData = new ArrayList<>();

  SlamLogSave(SlamCoreContainer slamCoreContainer, SlamPrcContainer slamPrcContainer, //
      PoseInterface poseInterface, SlamEventCounter slamEventCounter) {
    this.slamEventCounter = slamEventCounter;
    this.slamCoreContainer = slamCoreContainer;
    this.slamPrcContainer = slamPrcContainer;
    this.poseInterface = poseInterface;
    filename = SlamDvsConfig.eventCamera.slamCoreConfig.dvsConfig.logFilename();
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
