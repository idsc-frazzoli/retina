// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.tensor.Tensor;

/** to be used for saving data during SLAM algorithm runs. Should save csv files */
/* package */ class SlamLogCollection extends PeriodicSlamStep {
  private final GokartPoseInterface gokartPoseInterface;
  private final List<double[]> logData;

  protected SlamLogCollection(SlamContainer slamContainer, SlamConfig slamConfig, GokartPoseInterface gokartPoseInterface) {
    super(slamContainer, slamConfig.waypointUpdateRate);
    this.gokartPoseInterface = gokartPoseInterface;
    logData = new ArrayList<>();
  }

  @Override // from PeriodicSlamStep
  protected void periodicTask(int currentTimeStamp, int lastComputationTimeStamp) {
    savePoseEstimates(currentTimeStamp);
  }

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
}
