// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.tensor.Scalar;

public interface MPCSteering extends MPCControlUpdateListener, MPCStateProviderClient {
  Scalar getSteering(Scalar time);
}
