// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;

public enum SlamAlgoConfig {
  ;
  public static final List<DavisDvsListener> getListeners(String config, SlamConfig slamConfig, SlamContainer slamContainer, //
      GokartPoseInterface gokartLidarPose) {
    switch (config) {
    case "standardConfig":
      return standardConfig(slamConfig, slamContainer);
    case "lidarMappingMode":
      return lidarMappingMode(slamConfig, slamContainer, gokartLidarPose);
    case "reactiveMapMode":
      return reactiveMapMode(slamConfig, slamContainer);
    // TODO MG in localization mode we just dont have a mapping step
    default:
      return standardConfig(slamConfig, slamContainer);
    }
  }

  private static final List<DavisDvsListener> standardConfig(SlamConfig slamConfig, SlamContainer slamContainer) {
    List<DavisDvsListener> standardConfig = new CopyOnWriteArrayList<>();
    SlamImageToGokart slamImageToGokart = new SlamImageToGokart(slamConfig);
    SlamLocalizationStep slamLocalizationStepListener = new SlamLocalizationStep(slamConfig, slamContainer, slamImageToGokart);
    SlamMappingStep slamMappingStepListener = new SlamMappingStep(slamConfig, slamContainer, slamImageToGokart);
    SlamMapProcessing slamMapProcessingListener = new SlamMapProcessing(slamConfig, slamContainer);
    // ---
    standardConfig.add(slamImageToGokart);
    standardConfig.add(slamLocalizationStepListener);
    standardConfig.add(slamMappingStepListener);
    standardConfig.add(slamMapProcessingListener);
    return standardConfig;
  }

  private static final List<DavisDvsListener> lidarMappingMode(SlamConfig slamConfig, SlamContainer slamContainer, //
      GokartPoseInterface gokartLidarPose) {
    List<DavisDvsListener> standardConfig = new CopyOnWriteArrayList<>();
    SlamImageToGokart slamImageToGokart = new SlamImageToGokart(slamConfig);
    SlamLidarLocalization slamLidarLocalization = new SlamLidarLocalization(slamContainer, gokartLidarPose);
    SlamMappingStepLidar slamMappingStepLidarListener = new SlamMappingStepLidar(slamContainer, slamImageToGokart);
    SlamMapProcessing slamMapProcessingListener = new SlamMapProcessing(slamConfig, slamContainer);
    // ---
    standardConfig.add(slamImageToGokart);
    standardConfig.add(slamLidarLocalization);
    standardConfig.add(slamMappingStepLidarListener);
    standardConfig.add(slamMapProcessingListener);
    return standardConfig;
  }

  private static final List<DavisDvsListener> reactiveMapMode(SlamConfig slamConfig, SlamContainer slamContainer) {
    List<DavisDvsListener> standardConfig = new CopyOnWriteArrayList<>();
    SlamImageToGokart slamImageToGokart = new SlamImageToGokart(slamConfig);
    SlamLocalizationStep slamLocalizationStepListener = new SlamLocalizationStep(slamConfig, slamContainer, slamImageToGokart);
    SlamMappingStepReactive slamMappingStepReactiveListener = new SlamMappingStepReactive(slamConfig, slamContainer, slamImageToGokart);
    SlamMapProcessing slamMapProcessingListener = new SlamMapProcessing(slamConfig, slamContainer);
    // ---
    standardConfig.add(slamImageToGokart);
    standardConfig.add(slamLocalizationStepListener);
    standardConfig.add(slamMappingStepReactiveListener);
    standardConfig.add(slamMapProcessingListener);
    return standardConfig;
  }
}
