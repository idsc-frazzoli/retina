package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.owl.data.Stopwatch;

public class fakeNewsEstimator extends MPCStateEstimationProvider {
  protected fakeNewsEstimator(Stopwatch stopwatch) {
    super(stopwatch);
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
  }

  @Override
  void last() {
  }
}
