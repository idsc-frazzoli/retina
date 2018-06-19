// code by jph
package ch.ethz.idsc.owl.car.drift;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.owl.math.StateSpaceModels;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;

abstract class DriftFlows implements FlowsInterface, Serializable {
  // helper function
  static Collection<Flow> create(StateSpaceModel stateSpaceModel, int thetas) {
    if (thetas % 2 == 1)
      ++thetas;
    Collection<Flow> collection = new HashSet<>();
    for (Tensor theta : Subdivide.of(-20 * Math.PI / 180, 20 * Math.PI / 180, thetas)) {
      Tensor u = Tensors.of(theta, RealScalar.of(1815));
      collection.add(StateSpaceModels.createFlow(stateSpaceModel, u));
    }
    return collection;
  }
}
