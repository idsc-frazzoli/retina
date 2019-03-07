// code by mh
package ch.ethz.idsc.gokart.core.mpc;

@FunctionalInterface
/* package */ interface MPCStateProviderClient {
  void setStateEstimationProvider(MPCStateEstimationProvider mpcStateEstimationProvider);
}
