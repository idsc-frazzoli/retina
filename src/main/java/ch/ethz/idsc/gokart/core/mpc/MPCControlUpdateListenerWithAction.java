// code by mh
package ch.ethz.idsc.gokart.core.mpc;

public abstract class MPCControlUpdateListenerWithAction extends MPCControlUpdateListener {
  @Override
  void getControlAndPredictionSteps(ControlAndPredictionSteps controlAndPredictionSteps) {
    super.getControlAndPredictionSteps(controlAndPredictionSteps);
    doAction();
  }

  abstract void doAction();
}
