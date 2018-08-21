// code by mg
package ch.ethz.idsc.demo.mg.slam.algo.listener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;

// TODO MG maybe some ID field to determine which config is active
public enum SlamAlgoConfig {
  ;
  public static final List<DavisDvsListener> standardConfig(SlamConfig slamConfig, SlamContainer slamContainer) {
    List<DavisDvsListener> standardConfig = new CopyOnWriteArrayList<>();
    SlamImageToGokart slamImageToGokart = new SlamImageToGokart(slamConfig);
    SlamLocalizationStepListener slamLocalizationStepListener = new SlamLocalizationStepListener(slamConfig, slamContainer, slamImageToGokart);
    SlamMappingStepListener slamMappingStepListener = new SlamMappingStepListener(slamConfig, slamContainer, slamImageToGokart);
    SlamMapProcessingListener slamMapProcessingListener = new SlamMapProcessingListener(slamConfig, slamContainer);
    SlamTrajectoryPlanningListener slamTrajectoryPlanningListener = new SlamTrajectoryPlanningListener(slamConfig, slamContainer, slamMapProcessingListener);
    // ---
    standardConfig.add(slamImageToGokart);
    standardConfig.add(slamLocalizationStepListener);
    standardConfig.add(slamMappingStepListener);
    standardConfig.add(slamMapProcessingListener);
    standardConfig.add(slamTrajectoryPlanningListener);
    return standardConfig;
  }
}
