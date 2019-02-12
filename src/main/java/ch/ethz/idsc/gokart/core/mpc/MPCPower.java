// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Optional;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/* package */ abstract class MPCPower extends MPCControlUpdateListener {
  // TODO MH document
  abstract Optional<Tensor> getPower(Scalar time);
}
