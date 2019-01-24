// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.tensor.io.Timing;

/* package */ class FakeNewsEstimator extends MPCStateEstimationProvider {
  protected FakeNewsEstimator(Timing timing) {
    super(timing);
  }

  @Override
  public GokartState getState() {
    // this is only for testing
    float time = getTime().number().floatValue();
    return new GokartState(//
        time, //
        1 + time, //
        2 + time, //
        3 + time, //
        4 + time, //
        5 + time, //
        6 + time, //
        7 + time, //
        8 + time, //
        9 + time, //
        10 + time);
  }

  @Override
  void first() {
    // ---
  }

  @Override
  void last() {
    // ---
  }
}
