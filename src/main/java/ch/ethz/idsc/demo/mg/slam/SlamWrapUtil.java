// code by mg
package ch.ethz.idsc.demo.mg.slam;

import java.util.List;

import ch.ethz.idsc.demo.mg.filter.AbstractFilterHandler;
import ch.ethz.idsc.demo.mg.slam.core.SlamAlgoConfiguration;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
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
   * @param gokartOdometryPose */
  public static void initialize(SlamCoreContainer slamCoreContainer, //
      SlamPrcContainer slamPrcContainer, AbstractFilterHandler abstractFilterHandler, //
      GokartPoseInterface gokartLidarPose, //
      GokartPoseOdometryDemo gokartOdometryPose) {
    slamCoreContainer.initialize(gokartLidarPose.getPose());
    gokartOdometryPose.setPose(gokartLidarPose.getPose());
    List<DavisDvsListener> listeners = //
        SlamAlgoConfiguration.getListeners(slamCoreContainer, slamPrcContainer, gokartLidarPose, gokartOdometryPose);
    listeners.forEach(abstractFilterHandler::addListener);
  }
}
