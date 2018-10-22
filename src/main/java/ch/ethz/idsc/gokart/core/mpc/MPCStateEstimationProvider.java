package ch.ethz.idsc.gokart.core.mpc;

public interface MPCStateEstimationProvider {
  GokartState getStateEstimate();
}
