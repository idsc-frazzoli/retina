// code by mg
package ch.ethz.idsc.demo.mg.slam.algo.listener;

import java.util.Objects;

import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.demo.mg.slam.algo.SlamMappingStepUtil;

/* package */ abstract class AbstractSlamMappingStep extends AbstractSlamStep {
  protected final int relevantParticles;

  protected AbstractSlamMappingStep(SlamConfig slamConfig, SlamContainer slamContainer, SlamImageToGokart slamImageToGokart) {
    super(slamContainer, slamImageToGokart);
    relevantParticles = slamConfig.relevantParticles.number().intValue();
  }

  protected void updateOccurrenceMap() {
    if (Objects.nonNull(slamImageToGokart.getEventGokartFrame()))
      SlamMappingStepUtil.updateOccurrenceMap(slamContainer.getSlamParticles(), slamContainer.getOccurrenceMap(), //
          slamImageToGokart.getEventGokartFrame(), relevantParticles);
  }
}
