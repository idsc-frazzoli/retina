// code by mg
package ch.ethz.idsc.demo.mg.slam.core;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.demo.mg.slam.GokartPoseOdometryDemo;
import ch.ethz.idsc.demo.mg.slam.SlamCoreContainer;
import ch.ethz.idsc.demo.mg.slam.SlamPrcContainer;
import ch.ethz.idsc.demo.mg.slam.config.SlamDvsConfig;
import ch.ethz.idsc.demo.mg.slam.log.DvsTimerLogCollection;
import ch.ethz.idsc.demo.mg.slam.log.SlamEventCounter;
import ch.ethz.idsc.demo.mg.slam.log.TimerLogCollection;
import ch.ethz.idsc.demo.mg.slam.prc.SlamMapProcessing;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.retina.davis.DavisDvsListener;
import ch.ethz.idsc.retina.util.math.Magnitude;

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
  public static final List<DavisDvsListener> getListeners( //
      SlamCoreContainer slamCoreContainer, SlamPrcContainer slamPrcContainer, //
      GokartPoseInterface gokartLidarPose, GokartPoseOdometryDemo gokartPoseOdometry) {
    System.out.println(SlamDvsConfig.eventCamera.slamCoreConfig.slamAlgoConfig);
    List<DavisDvsListener> listeners = new ArrayList<>();
    /** image plane to go kart frame mapping is used by every configuration
     * and always the first module to be called */
    listeners.add(new SlamImageToGokart(slamCoreContainer));
    /** further modules depend on config */
    switch (SlamDvsConfig.eventCamera.slamCoreConfig.slamAlgoConfig) {
    case standardMode:
      standardMode(listeners, slamCoreContainer, slamPrcContainer);
      break;
    case standardReactiveMode:
      reactiveMapMode(listeners, slamCoreContainer, slamPrcContainer);
      break;
    case lidarMode:
      externalPoseMode(listeners, slamCoreContainer, slamPrcContainer, gokartLidarPose);
      break;
    case odometryMode:
      externalPoseMode(listeners, slamCoreContainer, slamPrcContainer, gokartPoseOdometry);
      break;
    case lidarReactiveMode:
      lidarPoseReactiveMode(listeners, slamCoreContainer, slamPrcContainer, gokartLidarPose);
      break;
    case odometryReactiveMode:
      odometryPoseReactiveMode(listeners, slamCoreContainer, slamPrcContainer, gokartPoseOdometry);
      break;
    case localizationMode:
      standardLocalizationStep(listeners, slamCoreContainer);
      break;
    default:
      throw new RuntimeException();
    }
    // we always use this module to move the map when pose is too close to boarders
    listeners.add(new SlamMapMove(slamCoreContainer));
    // log configuration
    if (SlamDvsConfig.eventCamera.slamCoreConfig.dvsTimeLogMode || SlamDvsConfig.eventCamera.slamCoreConfig.periodicLogMode) {
      SlamEventCounter slamEventCounter = new SlamEventCounter(slamCoreContainer);
      listeners.add(slamEventCounter);
      if (SlamDvsConfig.eventCamera.slamCoreConfig.dvsTimeLogMode)
        listeners.add(new DvsTimerLogCollection(slamCoreContainer, slamPrcContainer, gokartLidarPose, slamEventCounter));
      else
        listeners.add(new TimerLogCollection(slamCoreContainer, slamPrcContainer, gokartLidarPose, slamEventCounter));
    }
    return listeners;
  }

  /** standardMode: the particle velocity state is used for state propagation */
  private static final void standardMode(List<DavisDvsListener> listeners, SlamCoreContainer slamCoreContainer, //
      SlamPrcContainer slamPrcContainer) {
    standardLocalizationStep(listeners, slamCoreContainer);
    standardMappingStep(listeners, slamCoreContainer, slamPrcContainer);
  }

  /** reactiveMapMode: In comparison with standardConfig, the part of the map which is currently not seen by the vehicle
   * is cleared. This results in a "local" localization */
  private static final void reactiveMapMode(List<DavisDvsListener> listeners, //
      SlamCoreContainer slamCoreContainer, SlamPrcContainer slamPrcContainer) {
    standardLocalizationStep(listeners, slamCoreContainer);
    standardMappingStep(listeners, slamCoreContainer, slamPrcContainer);
    listeners.add(new SlamReactiveMapStep(slamCoreContainer));
  }

  /** externalPoseMode: Instead of using a particle filter, the pose is provided by an external module like the lidar
   * or odometry. The occurrence map is then updated using this pose */
  private static final void externalPoseMode(List<DavisDvsListener> listeners, SlamCoreContainer slamCoreContainer, //
      SlamPrcContainer slamPrcContainer, GokartPoseInterface gokartPoseInterface) {
    listeners.add(new SlamLocalizationStep(slamCoreContainer, gokartPoseInterface));
    listeners.add(new SlamMappingStep(slamCoreContainer));
    listeners.add(new SlamMapProcessing(slamCoreContainer, slamPrcContainer));
  }

  /** lidarPoseReactiveMode: reactive map mode with lidar pose. Since the lidar pose does not drift, we do not need a SlamMapPoseReset */
  private static final void lidarPoseReactiveMode(List<DavisDvsListener> listeners, SlamCoreContainer slamCoreContainer, //
      SlamPrcContainer slamPrcContainer, GokartPoseInterface gokartPoseInterface) {
    externalPoseMode(listeners, slamCoreContainer, slamPrcContainer, gokartPoseInterface);
    listeners.add(new SlamReactiveMapStep(slamCoreContainer));
  }

  /** odometryPoseReactiveMode: reactive mode with odometry. In comparison to lidarPoseReactiveMode, SlamPoseMapReset module is included
   * since the odometry pose tends to drift away */
  private static final void odometryPoseReactiveMode(List<DavisDvsListener> listeners, SlamCoreContainer slamCoreContainer, //
      SlamPrcContainer slamPrcContainer, GokartPoseOdometryDemo gokartPoseOdometry) {
    listeners.add(new SlamPoseOdometryStep(slamCoreContainer, gokartPoseOdometry));
    listeners.add(new SlamMappingStep(slamCoreContainer));
    listeners.add(new SlamReactiveMapStep(slamCoreContainer));
    listeners.add(new SlamMapProcessing(slamCoreContainer, slamPrcContainer));
  }

  /** localizationMode: the mapping step of the algorithm is not executed and a previously known ground truth map is loaded instead.
   * Only for measuring performance of particle filter for localization step, therefore no SlamMapProcessing is done */
  private static final void standardLocalizationStep(List<DavisDvsListener> listeners, SlamCoreContainer slamCoreContainer) {
    listeners.add(new SlamLikelihoodStep(slamCoreContainer));
    listeners.add(new SlamPropagationStep( //
        slamCoreContainer, //
        Magnitude.ONE.toInt(SlamDvsConfig.eventCamera.slamCoreConfig.particleRange)));
    listeners.add(new SlamResamplingStep(slamCoreContainer));
  }

  /** standard mapping step of SLAM algorithm. consists of three modules */
  private static final void standardMappingStep(List<DavisDvsListener> listeners, SlamCoreContainer slamCoreContainer, //
      SlamPrcContainer slamPrcContainer) {
    listeners.add(new SlamOccurrenceMapStep( //
        slamCoreContainer, //
        SlamDvsConfig.eventCamera.slamCoreConfig.relevantParticles.number().intValue()));
    listeners.add(new SlamMapProcessing(slamCoreContainer, slamPrcContainer));
  }
}
