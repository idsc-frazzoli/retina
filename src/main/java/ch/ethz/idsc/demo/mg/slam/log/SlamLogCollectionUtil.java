// code by mg
package ch.ethz.idsc.demo.mg.slam.log;

import java.util.List;

import ch.ethz.idsc.tensor.Tensor;

/** methods to save data from SLAM algorithm in csv files */
/* package */ enum SlamLogCollectionUtil {
  ;
  /** saves the SLAM pose estimate and ground truth pose
   * 
   * @param currentTimeStamp interpreted as [us]
   * @param groundTruthPose with units, provided by e.g. lidar
   * @param estimatedPose unitless
   * @param logData */
  public static void savePoseEstimates(int currentTimeStamp, Tensor groundTruthPose, //
      Tensor estimatedPose, List<double[]> logData) {
    double[] logInstant = new double[7];
    logInstant[0] = currentTimeStamp;
    for (int i = 0; i < 3; i++)
      logInstant[i + 1] = groundTruthPose.Get(i).number().doubleValue();
    for (int i = 0; i < 3; i++)
      logInstant[i + 4] = estimatedPose.Get(i).number().doubleValue();
    logData.add(logInstant);
  }
  // TODO could save other quantities as well
}
