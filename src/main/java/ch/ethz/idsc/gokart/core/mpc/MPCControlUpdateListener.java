// code by jph
package ch.ethz.idsc.gokart.core.mpc;

public interface MPCControlUpdateListener {
  /** @param controlAndPredictionSteps */
  void getControlAndPredictionSteps(ControlAndPredictionSteps controlAndPredictionSteps);
}
