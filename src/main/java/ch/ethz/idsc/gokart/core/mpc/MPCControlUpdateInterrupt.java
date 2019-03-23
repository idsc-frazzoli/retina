// code by mh
package ch.ethz.idsc.gokart.core.mpc;

/* package */ class MPCControlUpdateInterrupt extends MPCControlUpdateListener {
  private final Thread thread;

  public MPCControlUpdateInterrupt(Thread thread) {
    this.thread = thread;
  }

  @Override // from MPCControlUpdateListener
  public final void getControlAndPredictionSteps(ControlAndPredictionSteps controlAndPredictionSteps) {
    super.getControlAndPredictionSteps(controlAndPredictionSteps);
    thread.interrupt();
  }
}
