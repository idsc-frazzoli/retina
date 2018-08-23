// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;

/** SLAM algorithm module configuration. The order in the list is the order of the respective callback method calls */
// TODO MG odometry state propagation
/* package */ enum SlamAlgoConfig {
  ;
  public static final List<DavisDvsListener> getListeners(SlamConfig slamConfig, SlamContainer slamContainer, //
      GokartPoseInterface gokartLidarPose) {
    switch (slamConfig.slamAlgoConfig()) {
    case standardConfig:
      return standardConfig(slamConfig, slamContainer);
    case lidarMappingMode:
      return lidarMappingMode(slamConfig, slamContainer, gokartLidarPose);
    case reactiveMapMode:
      return reactiveMapMode(slamConfig, slamContainer);
    case localizationMode:
      return localizationMode(slamConfig, slamContainer);
    }
    throw new RuntimeException();
  }

  /** standardConfig: the particle velocity state is used for state propagation */
  private static final List<DavisDvsListener> standardConfig(SlamConfig slamConfig, SlamContainer slamContainer) {
    List<DavisDvsListener> listeners = new CopyOnWriteArrayList<>();
    SlamImageToGokart slamImageToGokart = new SlamImageToGokart(slamConfig, slamContainer);
    SlamLocalizationStep slamLocalizationStep = new SlamLocalizationStep(slamConfig, slamContainer);
    SlamMappingStep slamMappingStep = new SlamMappingStep(slamConfig, slamContainer);
    SlamMapProcessing slamMapProcessing = new SlamMapProcessing(slamConfig, slamContainer);
    // ---
    listeners.add(slamImageToGokart);
    listeners.add(slamLocalizationStep);
    listeners.add(slamMappingStep);
    listeners.add(slamMapProcessing);
    return listeners;
  }

  /** lidarMappingMode: The localization step of the algorithm is replaced with the provided lidar pose estimate.
   * Consequently, the map is being built using a "ground truth" pose */
  private static final List<DavisDvsListener> lidarMappingMode(SlamConfig slamConfig, SlamContainer slamContainer, //
      GokartPoseInterface gokartLidarPose) {
    List<DavisDvsListener> listeners = new CopyOnWriteArrayList<>();
    SlamImageToGokart slamImageToGokart = new SlamImageToGokart(slamConfig, slamContainer);
    SlamLidarLocalization slamLidarLocalization = new SlamLidarLocalization(slamContainer, gokartLidarPose);
    SlamMappingStepLidar slamMappingStepLidar = new SlamMappingStepLidar(slamContainer);
    SlamMapProcessing slamMapProcessing = new SlamMapProcessing(slamConfig, slamContainer);
    // ---
    listeners.add(slamImageToGokart);
    listeners.add(slamLidarLocalization);
    listeners.add(slamMappingStepLidar);
    listeners.add(slamMapProcessing);
    return listeners;
  }

  /** reactiveMapMode: In comparison with standardConfig, the part of the map which is currently not seen by the vehicle
   * is cleared. This results in a "local" localization. */
  private static final List<DavisDvsListener> reactiveMapMode(SlamConfig slamConfig, SlamContainer slamContainer) {
    List<DavisDvsListener> listeners = new CopyOnWriteArrayList<>();
    SlamImageToGokart slamImageToGokart = new SlamImageToGokart(slamConfig, slamContainer);
    SlamLocalizationStep slamLocalizationStep = new SlamLocalizationStep(slamConfig, slamContainer);
    SlamMappingStepReactive slamMappingStepReactive = new SlamMappingStepReactive(slamConfig, slamContainer);
    SlamMapProcessing slamMapProcessing = new SlamMapProcessing(slamConfig, slamContainer);
    // ---
    listeners.add(slamImageToGokart);
    listeners.add(slamLocalizationStep);
    listeners.add(slamMappingStepReactive);
    listeners.add(slamMapProcessing);
    return listeners;
  }

  /** localizationMode: the mapping mode of the algorithm is replaced by using a previously saved "ground truth" map. */
  private static List<DavisDvsListener> localizationMode(SlamConfig slamConfig, SlamContainer slamContainer) {
    List<DavisDvsListener> listeners = new CopyOnWriteArrayList<>();
    SlamImageToGokart slamImageToGokart = new SlamImageToGokart(slamConfig, slamContainer);
    SlamMappingStep slamMappingStep = new SlamMappingStep(slamConfig, slamContainer);
    // ---
    listeners.add(slamImageToGokart);
    listeners.add(slamMappingStep);
    return listeners;
  }
}
