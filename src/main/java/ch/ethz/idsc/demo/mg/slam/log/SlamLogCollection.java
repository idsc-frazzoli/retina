// code by mg
package ch.ethz.idsc.demo.mg.slam.log;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.demo.mg.slam.SlamCoreContainer;
import ch.ethz.idsc.demo.mg.slam.SlamFileLocations;
import ch.ethz.idsc.demo.mg.slam.SlamPrcContainer;
import ch.ethz.idsc.demo.mg.slam.config.SlamCoreConfig;
import ch.ethz.idsc.demo.mg.slam.core.PeriodicSlamStep;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.retina.util.StartAndStoppable;
import ch.ethz.idsc.tensor.Tensor;

/** save CSV logs when testing the SLAM algorithm offline */
public class SlamLogCollection extends PeriodicSlamStep implements StartAndStoppable {
  private final SlamPrcContainer slamPrcContainer;
  private final GokartPoseInterface gokartLidarPose;
  private final SlamEventCounter slamEventCounter;
  private final String filename;
  private final List<double[]> logData;

  public SlamLogCollection(SlamCoreContainer slamCoreContainer, SlamPrcContainer slamPrcContainer, //
      GokartPoseInterface gokartPoseInterface, SlamEventCounter slamEventCounter) {
    super(slamCoreContainer, SlamCoreConfig.GLOBAL.logCollectionUpdateRate);
    this.slamPrcContainer = slamPrcContainer;
    this.gokartLidarPose = gokartPoseInterface;
    this.slamEventCounter = slamEventCounter;
    filename = SlamCoreConfig.GLOBAL.davisConfig.logFilename();
    logData = new ArrayList<>();
  }

  @Override // from PeriodicSlamStep
  protected void periodicTask(int currentTimeStamp, int lastComputationTimeStamp) {
    // SlamLogCollectionUtil.savePoseEstimates(currentTimeStamp, gokartLidarPose.getPose(), //
    // slamCoreContainer.getPoseUnitless(), logData);
    // SlamLogCollectionUtil.saveProcessedEventCount(currentTimeStamp, slamEventCounter.getProcessedEvents(), //
    // slamEventCounter.getRawEvents(), logData);
    Tensor gokartWaypoints = slamPrcContainer.getGokartWaypoints();
    double xDistance = gokartWaypoints.get(gokartWaypoints.length() - 1).Get(0).number().doubleValue();
    SlamLogCollectionUtil.saveWaypointDistance(currentTimeStamp, xDistance, logData);
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
