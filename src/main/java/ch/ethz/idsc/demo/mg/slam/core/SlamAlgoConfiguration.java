// code by mg
package ch.ethz.idsc.demo.mg.slam.core;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.demo.mg.slam.GokartPoseOdometryDemo;
import ch.ethz.idsc.demo.mg.slam.SlamCoreContainer;
import ch.ethz.idsc.demo.mg.slam.SlamPrcContainer;
import ch.ethz.idsc.demo.mg.slam.config.SlamCoreConfig;
import ch.ethz.idsc.demo.mg.slam.log.SlamLogCollection;
import ch.ethz.idsc.demo.mg.slam.prc.SlamMapProcessing;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;

/** SLAM algorithm module configuration.
 * The order in the list is the order of the respective callback method calls
 * 
 * implementation of the SLAM algorithm
 * "simultaneous localization and mapping for event-based vision systems"
 * by David Weikersdorfer, Raoul Hoffmann, and Joerg Conradt
 * https://mediatum.ub.tum.de/doc/1191908/1191908.pdf
 * all modules of the SLAM algorithm implement {@link DavisDvsListener} and are contained
 * in the field listeners */
public enum SlamAlgoConfiguration {
  ;
  public static final List<DavisDvsListener> getListeners(SlamCoreContainer slamContainer, SlamPrcContainer slamCurveContainer, SlamCoreConfig slamConfig, //
      GokartPoseInterface gokartLidarPose, GokartPoseOdometryDemo gokartPoseOdometry) {
    System.out.println(slamConfig.slamAlgoConfig);
    List<DavisDvsListener> listeners = new ArrayList<>();
    /** image plane to go kart frame mapping is used by every configuration
     * and always the first module to be called */
    listeners.add(new SlamImageToGokart(slamContainer, slamConfig));
    /** further modules depend on config */
    switch (slamConfig.slamAlgoConfig) {
    case standardMode:
      standardMode(listeners, slamContainer, slamCurveContainer, slamConfig);
      break;
    case standardReactiveMode:
      reactiveMapMode(listeners, slamContainer, slamCurveContainer, slamConfig);
      break;
    case lidarMode:
      externalPoseMode(listeners, slamContainer, slamCurveContainer, slamConfig, gokartLidarPose);
      break;
    case odometryMode:
      externalPoseMode(listeners, slamContainer, slamCurveContainer, slamConfig, gokartPoseOdometry);
      break;
    case lidarReactiveMode:
      lidarPoseReactiveMode(listeners, slamContainer, slamCurveContainer, slamConfig, gokartLidarPose);
      break;
    case odometryReactiveMode:
      odometryPoseReactiveMode(listeners, slamContainer, slamCurveContainer, slamConfig, gokartPoseOdometry);
      break;
    case localizationMode:
      standardLocalizationStep(listeners, slamContainer, slamConfig);
      break;
    default:
      throw new RuntimeException();
    }
    if (slamConfig.offlineLogMode)
      listeners.add(new SlamLogCollection(slamContainer, slamConfig, gokartLidarPose));
    return listeners;
  }

  /** standardMode: the particle velocity state is used for state propagation */
  private static final void standardMode(List<DavisDvsListener> listeners, SlamCoreContainer slamContainer, //
      SlamPrcContainer slamCurveContainer, SlamCoreConfig slamConfig) {
    standardLocalizationStep(listeners, slamContainer, slamConfig);
    standardMappingStep(listeners, slamContainer, slamCurveContainer, slamConfig);
  }

  /** reactiveMapMode: In comparison with standardConfig, the part of the map which is currently not seen by the vehicle
   * is cleared. This results in a "local" localization */
  private static final void reactiveMapMode(List<DavisDvsListener> listeners, //
      SlamCoreContainer slamContainer, SlamPrcContainer slamCurveContainer, SlamCoreConfig slamConfig) {
    standardLocalizationStep(listeners, slamContainer, slamConfig);
    standardMappingStep(listeners, slamContainer, slamCurveContainer, slamConfig);
    listeners.add(new SlamReactiveMapStep(slamConfig, slamContainer));
  }

  /** externalPoseMode: Instead of using a particle filter, the pose is provided by an external module like the lidar
   * or odometry. The occurrence map is then updated using this pose */
  private static final void externalPoseMode(List<DavisDvsListener> listeners, SlamCoreContainer slamContainer, //
      SlamPrcContainer slamCurveContainer, SlamCoreConfig slamConfig, GokartPoseInterface gokartPoseInterface) {
    listeners.add(new SlamLocalizationStep(slamContainer, slamConfig, gokartPoseInterface));
    listeners.add(new SlamMappingStep(slamContainer));
    listeners.add(new SlamMapProcessing(slamContainer, slamCurveContainer));
  }

  /** lidarPoseReactiveMode: reactive map mode with lidar pose. Since the lidar pose does not drift, we do not need a SlamMapPoseReset */
  private static final void lidarPoseReactiveMode(List<DavisDvsListener> listeners, SlamCoreContainer slamContainer, //
      SlamPrcContainer slamCurveContainer, SlamCoreConfig slamConfig, GokartPoseInterface gokartPoseInterface) {
    externalPoseMode(listeners, slamContainer, slamCurveContainer, slamConfig, gokartPoseInterface);
    listeners.add(new SlamReactiveMapStep(slamConfig, slamContainer));
  }

  /** odometryPoseReactiveMode: reactive mode with odometry. In comparison to lidarPoseReactiveMode, SlamPoseMapReset module is included
   * since the odometry pose tends to drift away */
  private static final void odometryPoseReactiveMode(List<DavisDvsListener> listeners, SlamCoreContainer slamContainer, //
      SlamPrcContainer slamCurveContainer, SlamCoreConfig slamConfig, GokartPoseOdometryDemo gokartPoseOdometry) {
    listeners.add(new SlamPoseOdometryStep(slamContainer, slamConfig, gokartPoseOdometry));
    listeners.add(new SlamMappingStep(slamContainer));
    listeners.add(new SlamReactiveMapStep(slamConfig, slamContainer));
    listeners.add(new SlamMapProcessing(slamContainer, slamCurveContainer));
    listeners.add(new SlamPoseMapReset(slamContainer, slamConfig));
  }

  /** localizationMode: the mapping step of the algorithm is not executed and a previously known ground truth map is loaded instead.
   * Only for measuring performance of particle filter for localization step, therefore no SlamMapProcessing is done */
  private static final void standardLocalizationStep(List<DavisDvsListener> listeners, SlamCoreContainer slamContainer, SlamCoreConfig slamConfig) {
    listeners.add(new SlamLikelihoodStep(slamContainer, slamConfig.alpha));
    listeners.add(new SlamPropagationStep(slamContainer, slamConfig));
    listeners.add(new SlamResamplingStep(slamContainer, slamConfig));
  }

  /** standard mapping step of SLAM algorithm. consists of three modules */
  private static final void standardMappingStep(List<DavisDvsListener> listeners, SlamCoreContainer slamContainer, //
      SlamPrcContainer slamCurveContainer, SlamCoreConfig slamConfig) {
    listeners.add(new SlamOccurrenceMapStep(slamContainer, slamConfig.relevantParticles));
    listeners.add(new SlamMapProcessing(slamContainer, slamCurveContainer));
    listeners.add(new SlamPoseMapReset(slamContainer, slamConfig));
  }
}
