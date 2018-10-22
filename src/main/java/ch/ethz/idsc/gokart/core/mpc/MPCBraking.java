package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.tensor.Scalar;

public interface MPCBraking extends MPCStateReceiver, MPCControlUpdateListener {
  Scalar getBraking(Scalar time);
}
