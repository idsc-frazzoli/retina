// code by mh
package ch.ethz.idsc.gokart.core.mpc;

@FunctionalInterface
/* package */ interface MPCStateProviderClient {
  /** @param mpcStateEstimationProvider */
  void setStateEstimationProvider(MPCStateEstimationProvider mpcStateEstimationProvider);
}
