// code by mg
package ch.ethz.idsc.demo.mg.slam.core;

import ch.ethz.idsc.demo.mg.slam.config.SlamDvsConfig;
import ch.ethz.idsc.retina.util.math.Magnitude;

/* package */ enum DavisSlamImageToGokartUtil {
  ;
  // TODO here we assume the only two options are "davis" and "sEye"
  // private static final boolean useDavis = SlamDvsConfig.cameraType.equals("davis");
  private static final double lookAheadDistance = Magnitude.METER.toDouble(SlamDvsConfig.eventCamera.slamCoreConfig.lookAheadDistance);
  // private static final double cropLowerPart = useDavis ? 0 : Magnitude.METER.toDouble(SlamCoreConfig.GLOBAL.cropLowerPart);
  // private static final double cropSides = useDavis ? 0 : Magnitude.METER.toDouble(SlamCoreConfig.GLOBAL.cropSides);

  /** @param eventGokartFrame go-kart frame
   * @return true when event is outside regarded region */
  public static boolean checkEventPosition(double[] eventGokartFrame) {
    // if (useDavis)
    return eventGokartFrame[0] > lookAheadDistance;
    // ---
    // return eventGokartFrame[0] > lookAheadDistance //
    // || eventGokartFrame[0] < cropLowerPart //
    // || eventGokartFrame[1] < -cropSides //
    // || eventGokartFrame[1] > cropSides;
  }
}
