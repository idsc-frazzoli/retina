// code by mh
package ch.ethz.idsc.gokart.core.mpc;

@FunctionalInterface
public interface MPCStateProviderClient {
  void setStateProvider(MPCStateEstimationProvider mpcstateProvider);
}
