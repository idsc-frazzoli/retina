package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public interface MPCPower extends MPCControlUpdateListener, MPCStateProviderClient {
  Tensor getPower(Scalar time);
}
