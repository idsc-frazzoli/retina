package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.tensor.Scalar;

public interface MPCBraking extends MPCControlUpdateListener, MPCStateProviderClient {
  Scalar getBraking(Scalar time);
}
