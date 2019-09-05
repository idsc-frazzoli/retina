// code by mg
package ch.ethz.idsc.retina.app.slam.core;

import ch.ethz.idsc.retina.app.slam.config.SlamDvsConfig;
import ch.ethz.idsc.retina.util.math.Magnitude;

/* package */ enum DavisSlamImageToGokartUtil {
  ;
  private static final double lookAheadDistance = Magnitude.METER.toDouble(SlamDvsConfig.eventCamera.slamCoreConfig.lookAheadDistance);

  /** @param eventGokartFrame go-kart frame
   * @return true when event is outside regarded region */
  public static boolean checkEventPosition(double[] eventGokartFrame) {
    return eventGokartFrame[0] > lookAheadDistance;
  }
}
