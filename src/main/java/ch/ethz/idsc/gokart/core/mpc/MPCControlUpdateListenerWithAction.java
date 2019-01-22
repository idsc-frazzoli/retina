// code by mh
package ch.ethz.idsc.gokart.core.mpc;

/* package */ abstract class MPCControlUpdateListenerWithAction extends MPCControlUpdateListener {
  @Override // from MPCControlUpdateListener
  final void getControlAndPredictionSteps(ControlAndPredictionSteps controlAndPredictionSteps) {
    super.getControlAndPredictionSteps(controlAndPredictionSteps);
    doAction();
  }

  abstract void doAction();
}
