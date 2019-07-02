// code by mg
package ch.ethz.idsc.retina.app.slam;

import java.util.List;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.retina.app.filter.AbstractFilterHandler;
import ch.ethz.idsc.retina.app.slam.core.SlamAlgoConfiguration;
import ch.ethz.idsc.retina.davis.DavisDvsListener;

/* package */ enum SlamWrapUtil {
  ;
  /** initializes the modules of the SLAM algorithm according to the configuration set in
   * slamConfig
   * 
   * @param slamCoreContainer contains fields shared between SLAM algorithm modules
   * @param slamPrcContainer
   * @param abstractFilterHandler sets up listeners
   * @param gokartLidarPose
   * @param gokartPoseOdometryDemo */
  public static void initialize( //
      SlamCoreContainer slamCoreContainer, //
      SlamPrcContainer slamPrcContainer, //
      AbstractFilterHandler abstractFilterHandler, //
      GokartPoseEvent gokartLidarPose, //
      GokartPoseOdometryDemo gokartPoseOdometryDemo) {
    slamCoreContainer.initialize(gokartLidarPose.getPose());
    gokartPoseOdometryDemo.setPose(gokartLidarPose.getPose());
    List<DavisDvsListener> listeners = //
        SlamAlgoConfiguration.getListeners(slamCoreContainer, slamPrcContainer, gokartLidarPose, gokartPoseOdometryDemo);
    listeners.forEach(abstractFilterHandler::addListener);
  }
}
