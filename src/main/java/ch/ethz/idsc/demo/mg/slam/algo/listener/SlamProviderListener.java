// code by mg
package ch.ethz.idsc.demo.mg.slam.algo.listener;

import ch.ethz.idsc.demo.mg.filter.BackgroundActivityFilter;
import ch.ethz.idsc.demo.mg.filter.AbstractFilterHandler;
import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamContainer;

/** implementation of the SLAM algorithm
 * "simultaneous localization and mapping for event-based vision systems"
 * by David Weikersdorfer, Raoul Hoffmann, and Joerg Conradt
 * https://mediatum.ub.tum.de/doc/1191908/1191908.pdf */
// TODO put in abstract class to include cases lidarMappingMode, odometryVel propagation, localizationMode
public class SlamProviderListener {
  private final SlamContainer slamContainer;
  private final AbstractFilterHandler filterHandler;
  private final SlamImageToGokart slamImageToGokart;
  private final SlamLocalizationStepListener slamLocalizationStepListener;
  private final SlamMappingStepListener slamMappingStepListener;
  private final SlamMapProcessingListener slamMapProcessingListener;
  private final SlamTrajectoryPlanningListener slamTrajectoryPlanningListener;

  public SlamProviderListener(SlamConfig slamConfig) {
    slamContainer = new SlamContainer(slamConfig);
    filterHandler = new BackgroundActivityFilter(slamConfig.davisConfig);
    slamImageToGokart = new SlamImageToGokart(slamConfig);
    slamLocalizationStepListener = new SlamLocalizationStepListener(slamConfig, slamContainer, slamImageToGokart);
    slamMappingStepListener = new SlamMappingStepListener(slamConfig, slamContainer, slamImageToGokart);
    slamMapProcessingListener = new SlamMapProcessingListener(slamConfig, slamContainer);
    slamTrajectoryPlanningListener = new SlamTrajectoryPlanningListener(slamConfig, slamContainer, slamMapProcessingListener);
    setupListeners();
  }

  private void setupListeners() {
    filterHandler.addListener(slamImageToGokart);
    filterHandler.addListener(slamLocalizationStepListener);
    filterHandler.addListener(slamMappingStepListener);
    filterHandler.addListener(slamMapProcessingListener);
    filterHandler.addListener(slamTrajectoryPlanningListener);
  }

  public AbstractFilterHandler getFilterHandler() {
    return filterHandler;
  }

  public SlamContainer getSlamContainer() {
    return slamContainer;
  }
}
