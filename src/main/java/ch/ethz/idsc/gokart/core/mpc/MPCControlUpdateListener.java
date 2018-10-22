package ch.ethz.idsc.gokart.core.mpc;

public interface MPCControlUpdateListener {
  void getControlAndPredictionSteps(ControlAndPredictionSteps controlAndPredictionSteps);
}
