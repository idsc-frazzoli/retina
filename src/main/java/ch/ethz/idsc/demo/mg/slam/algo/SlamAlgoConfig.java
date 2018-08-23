// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.ethz.idsc.demo.mg.slam.GokartPoseOdometryDemo;
import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;

/** SLAM algorithm module configuration. The order in the list is the order of the respective callback method calls */
/* package */ enum SlamAlgoConfig {
  ;
  public static final List<DavisDvsListener> getListeners(SlamConfig slamConfig, SlamContainer slamContainer, //
      GokartPoseInterface gokartLidarPose, GokartPoseOdometryDemo gokartPoseOdometry) {
    switch (slamConfig.slamAlgoConfig()) {
    case standardMode:
      return standardMode(slamConfig, slamContainer);
    case lidarMode:
      return externalPoseMode(slamConfig, slamContainer, gokartLidarPose);
    case reactiveMapMode:
      return reactiveMapMode(slamConfig, slamContainer);
    case odometryMode:
      return externalPoseMode(slamConfig, slamContainer, gokartPoseOdometry);
    case localizationMode:
      return localizationMode(slamConfig, slamContainer);
    }
    throw new RuntimeException();
  }

  /** standardMode: the particle velocity state is used for state propagation */
  private static final List<DavisDvsListener> standardMode(SlamConfig slamConfig, SlamContainer slamContainer) {
    List<DavisDvsListener> listeners = new CopyOnWriteArrayList<>();
    SlamImageToGokart slamImageToGokart = new SlamImageToGokart(slamConfig, slamContainer);
    SlamLikelihoodStep slamLikelihoodStep = new SlamLikelihoodStep(slamConfig, slamContainer);
    SlamPropagationStep slamPropagationStep = new SlamPropagationStep(slamConfig, slamContainer);
    SlamResamplingStep slamResamplingStep = new SlamResamplingStep(slamConfig, slamContainer);
    SlamOccurrenceMapStep slamOccurrenceMapStep = new SlamOccurrenceMapStep(slamConfig, slamContainer);
    SlamMapProcessing slamMapProcessing = new SlamMapProcessing(slamConfig, slamContainer);
    // ---
    listeners.add(slamImageToGokart);
    listeners.add(slamLikelihoodStep);
    listeners.add(slamPropagationStep);
    listeners.add(slamResamplingStep);
    listeners.add(slamOccurrenceMapStep);
    listeners.add(slamMapProcessing);
    return listeners;
  }

  /** externalPoseMode: Instead of using a particle filter, the pose is provided by an external module like the lidar
   * or odometry. The occurrence map is then updated using this pose */
  private static final List<DavisDvsListener> externalPoseMode(SlamConfig slamConfig, SlamContainer slamContainer, //
      GokartPoseInterface gokartPoseInterface) {
    List<DavisDvsListener> listeners = new CopyOnWriteArrayList<>();
    SlamImageToGokart slamImageToGokart = new SlamImageToGokart(slamConfig, slamContainer);
    SlamLocalizationStep slamLocalizationStep = new SlamLocalizationStep(slamContainer, gokartPoseInterface);
    SlamMappingStep slamMappingStep = new SlamMappingStep(slamContainer);
    SlamMapProcessing slamMapProcessing = new SlamMapProcessing(slamConfig, slamContainer);
    // ---
    listeners.add(slamImageToGokart);
    listeners.add(slamLocalizationStep);
    listeners.add(slamMappingStep);
    listeners.add(slamMapProcessing);
    return listeners;
  }

  /** reactiveMapMode: In comparison with standardConfig, the part of the map which is currently not seen by the vehicle
   * is cleared. This results in a "local" localization. */
  private static final List<DavisDvsListener> reactiveMapMode(SlamConfig slamConfig, SlamContainer slamContainer) {
    List<DavisDvsListener> listeners = new CopyOnWriteArrayList<>();
    SlamImageToGokart slamImageToGokart = new SlamImageToGokart(slamConfig, slamContainer);
    SlamLikelihoodStep slamLikelihoodStep = new SlamLikelihoodStep(slamConfig, slamContainer);
    SlamPropagationStep slamPropagationStep = new SlamPropagationStep(slamConfig, slamContainer);
    SlamResamplingStep slamResamplingStep = new SlamResamplingStep(slamConfig, slamContainer);
    SlamOccurrenceMapStep slamOccurrenceMapStep = new SlamOccurrenceMapStep(slamConfig, slamContainer);
    SlamReactiveMapStep slamMappingStepReactive = new SlamReactiveMapStep(slamConfig, slamContainer);
    SlamMapProcessing slamMapProcessing = new SlamMapProcessing(slamConfig, slamContainer);
    // ---
    listeners.add(slamImageToGokart);
    listeners.add(slamLikelihoodStep);
    listeners.add(slamPropagationStep);
    listeners.add(slamResamplingStep);
    listeners.add(slamOccurrenceMapStep);
    listeners.add(slamMappingStepReactive);
    listeners.add(slamMapProcessing);
    return listeners;
  }

  /** localizationMode: the mapping mode of the algorithm is replaced by using a previously saved "ground truth" map. */
  private static List<DavisDvsListener> localizationMode(SlamConfig slamConfig, SlamContainer slamContainer) {
    List<DavisDvsListener> listeners = new CopyOnWriteArrayList<>();
    SlamImageToGokart slamImageToGokart = new SlamImageToGokart(slamConfig, slamContainer);
    SlamLikelihoodStep slamLikelihoodStep = new SlamLikelihoodStep(slamConfig, slamContainer);
    SlamPropagationStep slamPropagationStep = new SlamPropagationStep(slamConfig, slamContainer);
    SlamResamplingStep slamResamplingStep = new SlamResamplingStep(slamConfig, slamContainer);
    // ---
    listeners.add(slamImageToGokart);
    listeners.add(slamLikelihoodStep);
    listeners.add(slamPropagationStep);
    listeners.add(slamResamplingStep);
    return listeners;
  }
}
