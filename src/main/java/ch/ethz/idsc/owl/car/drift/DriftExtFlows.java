// code by edo
package ch.ethz.idsc.owl.car.drift;

import java.util.Collection;

import ch.ethz.idsc.owl.math.flow.Flow;

class DriftExtFlows extends DriftFlows {
  @Override // from FlowsInterface
  public Collection<Flow> getFlows(int resolution) {
    return create(new DriftExtStateSpaceModel(new DriftParameters()), resolution);
  }
}
