// code by mh
package ch.ethz.idsc.gokart.core.mpc;

/* package */ class MPCControlUpdateInterrupt implements MPCControlUpdateListener {
  private final Thread thread;

  public MPCControlUpdateInterrupt(Thread thread) {
    this.thread = thread;
  }

  @Override // from MPCControlUpdateListener
  public void getControlAndPredictionSteps(ControlAndPredictionSteps controlAndPredictionSteps) {
    thread.interrupt();
  }
}
