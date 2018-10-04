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

  /** saves count of processed and filtered events
   * 
   * @param currentTimeStamp interpreted as [us]
   * @param processedEventCount events processed after filtering
   * @param totalEventCount events published to filter module
   * @param logData */
  public static void saveProcessedEventCount(int currentTimeStamp, long processedEventCount, long totalEventCount, List<double[]> logData) {
    double[] logInstant = new double[3];
    logInstant[0] = currentTimeStamp;
    logInstant[1] = processedEventCount;
    logInstant[2] = totalEventCount;
    logData.add(logInstant);
  }

  public static void saveWaypointDistance(int currentTimeStamp, double xDistance, List<double[]> logData) {
    double[] logInstant = new double[2];
    logInstant[0] = currentTimeStamp;
    logInstant[1] = xDistance;
    logData.add(logInstant);
  }
}
