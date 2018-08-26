// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import java.util.List;

import ch.ethz.idsc.demo.mg.filter.AbstractFilterHandler;
import ch.ethz.idsc.demo.mg.slam.GokartPoseOdometryDemo;
import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;

/** implementation of the SLAM algorithm
 * "simultaneous localization and mapping for event-based vision systems"
 * by David Weikersdorfer, Raoul Hoffmann, and Joerg Conradt
 * https://mediatum.ub.tum.de/doc/1191908/1191908.pdf
 * all modules of the algorithm implement {@link DavisDvsListener} and are contained
 * in the field listeners */
public class SlamProvider {
  private final SlamContainer slamContainer;

  public SlamProvider( //
      SlamConfig slamConfig, //
      AbstractFilterHandler abstractFilterHandler, //
      GokartPoseInterface gokartLidarPose, //
      GokartPoseOdometryDemo gokartPoseOdometryDemo) {
    slamContainer = new SlamContainer(slamConfig);
    List<DavisDvsListener> listeners = //
        SlamAlgoConfig.getListeners(slamConfig, slamContainer, gokartLidarPose, gokartPoseOdometryDemo);
    listeners.forEach(abstractFilterHandler::addListener);
  }

  public SlamContainer getSlamContainer() {
    return slamContainer;
  }
}
