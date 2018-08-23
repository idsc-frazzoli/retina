// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import ch.ethz.idsc.demo.mg.slam.SlamContainer;

/** abstract class for SLAM mapping step implementations */
/* package */ abstract class AbstractSlamMappingStep extends AbstractSlamStep {
  protected AbstractSlamMappingStep(SlamContainer slamContainer) {
    super(slamContainer);
  }

  protected abstract void updateOccurrenceMap();
}
