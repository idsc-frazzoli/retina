// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import ch.ethz.idsc.demo.mg.slam.SlamContainer;

/* package */ abstract class AbstractSlamMappingStep extends AbstractSlamStep {
  protected SlamImageToGokart slamImageToGokart;

  protected AbstractSlamMappingStep(SlamContainer slamContainer, SlamImageToGokart slamImageToGokart) {
    super(slamContainer);
    this.slamImageToGokart = slamImageToGokart;
  }

  abstract protected void updateOccurrenceMap();
}
