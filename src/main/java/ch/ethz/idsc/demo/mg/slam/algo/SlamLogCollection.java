// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.demo.mg.slam.SlamFileLocations;
import ch.ethz.idsc.demo.mg.util.io.CsvIO;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.retina.util.StartAndStoppable;
import ch.ethz.idsc.tensor.Tensor;

/** save CSV logs when testing the SLAM algorithm offline */
/* package */ class SlamLogCollection extends PeriodicSlamStep implements StartAndStoppable {
  private final GokartPoseInterface gokartPoseInterface;
  private final String filename;
  private final List<double[]> logData;

  protected SlamLogCollection(SlamContainer slamContainer, SlamConfig slamConfig, GokartPoseInterface gokartPoseInterface) {
    super(slamContainer, slamConfig.logCollectionUpdateRate);
    this.gokartPoseInterface = gokartPoseInterface;
    filename = slamConfig.davisConfig.logFilename();
    logData = new ArrayList<>();
  }

  @Override // from PeriodicSlamStep
  protected void periodicTask(int currentTimeStamp, int lastComputationTimeStamp) {
    savePoseEstimates(currentTimeStamp);
  }

  /** saves 7 doubles per instant, consisting of currentTimeStamp [us],
   * lidar pose and SLAM algorithm pose estimate */
  private void savePoseEstimates(int currentTimeStamp) {
    double[] logInstant = new double[7];
    Tensor groundTruthPose = gokartPoseInterface.getPose();
    Tensor estimatedPose = slamContainer.getPose();
    logInstant[0] = currentTimeStamp;
    for (int i = 0; i < 3; i++)
      logInstant[i + 1] = groundTruthPose.Get(i).number().doubleValue();
    for (int i = 0; i < 3; i++)
      logInstant[i + 4] = estimatedPose.Get(i).number().doubleValue();
    logData.add(logInstant);
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
