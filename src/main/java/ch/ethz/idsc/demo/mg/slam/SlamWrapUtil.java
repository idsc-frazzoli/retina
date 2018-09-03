// code by mg
package ch.ethz.idsc.demo.mg.slam;

import java.util.List;

import ch.ethz.idsc.demo.mg.filter.AbstractFilterHandler;
import ch.ethz.idsc.demo.mg.slam.algo.SlamAlgoConfig;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;

/* package */ enum SlamWrapUtil {
  ;
  /** initializes the modules of the SLAM algorithm according to the configuration set in
   * slamConfig
   * 
   * @param slamConfig parameters for SLAM algorithm
   * @param slamContainer contains fields shared between SLAM algorithm modules
   * @param abstractFilterHandler sets up listeners
   * @param gokartLidarPose
   * @param gokartOdometryPose */
  public static void initialize(SlamConfig slamConfig, //
      SlamContainer slamContainer, //
      AbstractFilterHandler abstractFilterHandler, //
      GokartPoseInterface gokartLidarPose, //
      GokartPoseOdometryDemo gokartOdometryPose) {
    slamContainer.initialize(gokartLidarPose.getPose());
    gokartOdometryPose.setPose(gokartLidarPose.getPose());
    List<DavisDvsListener> listeners = //
        SlamAlgoConfig.getListeners(slamContainer, slamConfig, gokartLidarPose, gokartOdometryPose);
    listeners.forEach(abstractFilterHandler::addListener);
  }
}
