//code by mh
package ch.ethz.idsc.gokart.core.mpc;

/* package */ class MPCOptimizationParameterMessage {
  public final int messageType = MPCNative.PARAMETER_UPDATE;
  public final MPCOptimizationParameter optimizationParameters;

  public MPCOptimizationParameterMessage(MPCOptimizationParameter optimizationParameters) {
    this.optimizationParameters = optimizationParameters;
  }
}
