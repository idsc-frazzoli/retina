// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Optional;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/* package */ abstract class MPCPower extends MPCControlUpdateCapture {
  /** @param time with unit "s"
   * @return vector with 2 entries with unit ARMS, or empty */
  abstract Optional<Tensor> getPower(Scalar time);
}
