// code by jph
package ch.ethz.idsc.gokart.core.mpc;

@FunctionalInterface
public interface MPCControlUpdateListener {
  /** @param controlAndPredictionSteps */
  void getControlAndPredictionSteps(ControlAndPredictionSteps controlAndPredictionSteps);
}
