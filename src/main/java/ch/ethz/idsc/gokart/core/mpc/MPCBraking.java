package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.tensor.Scalar;

public interface MPCBraking extends MPCStateReceiver{
  void Update(ControlAndPredictionSteps controlAndPredictionSteps);

  Scalar getBraking(Scalar time);
}
