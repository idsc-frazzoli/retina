package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.tensor.Scalar;

public interface MPCSteering extends MPCStateReceiver, MPCControlUpdateListener {
  Scalar getSteering(Scalar time);
}
