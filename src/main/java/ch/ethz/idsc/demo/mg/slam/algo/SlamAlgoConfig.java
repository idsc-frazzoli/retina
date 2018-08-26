// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import java.util.Arrays;
import java.util.List;

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
    System.out.println(slamConfig.slamAlgoConfig());
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
    return Arrays.asList( //
        new SlamImageToGokart(slamConfig, slamContainer), //
        new SlamLikelihoodStep(slamConfig, slamContainer), //
        new SlamPropagationStep(slamConfig, slamContainer), //
        new SlamResamplingStep(slamConfig, slamContainer), //
        new SlamOccurrenceMapStep(slamConfig, slamContainer), //
        new SlamMapProcessing(slamConfig, slamContainer));
  }

  /** externalPoseMode: Instead of using a particle filter, the pose is provided by an external module like the lidar
   * or odometry. The occurrence map is then updated using this pose */
  private static final List<DavisDvsListener> externalPoseMode( //
      SlamConfig slamConfig, SlamContainer slamContainer, GokartPoseInterface gokartPoseInterface) {
    return Arrays.asList( //
        new SlamImageToGokart(slamConfig, slamContainer), //
        new SlamLocalizationStep(slamContainer, gokartPoseInterface), //
        new SlamMappingStep(slamContainer), //
        new SlamMapProcessing(slamConfig, slamContainer));
  }

  /** reactiveMapMode: In comparison with standardConfig, the part of the map which is currently not seen by the vehicle
   * is cleared. This results in a "local" localization. */
  private static final List<DavisDvsListener> reactiveMapMode(SlamConfig slamConfig, SlamContainer slamContainer) {
    return Arrays.asList( //
        new SlamImageToGokart(slamConfig, slamContainer), //
        new SlamLikelihoodStep(slamConfig, slamContainer), //
        new SlamPropagationStep(slamConfig, slamContainer), //
        new SlamResamplingStep(slamConfig, slamContainer), //
        new SlamOccurrenceMapStep(slamConfig, slamContainer), //
        new SlamReactiveMapStep(slamConfig, slamContainer), //
        new SlamMapProcessing(slamConfig, slamContainer));
  }

  /** localizationMode: the mapping mode of the algorithm is replaced by using a previously saved "ground truth" map. */
  private static List<DavisDvsListener> localizationMode(SlamConfig slamConfig, SlamContainer slamContainer) {
    return Arrays.asList( //
        new SlamImageToGokart(slamConfig, slamContainer), //
        new SlamLikelihoodStep(slamConfig, slamContainer), //
        new SlamPropagationStep(slamConfig, slamContainer), //
        new SlamResamplingStep(slamConfig, slamContainer));
  }
}
