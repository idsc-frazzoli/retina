//code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.io.Serializable;

public class MPCOptimizationParameterMessage implements Serializable {
  private static final long serialVersionUID = 1L;
  public final int messageType = MPCNative.PARAMETER_UPDATE;
  public final MPCOptimizationParameter optimizationParameters;

  public MPCOptimizationParameterMessage(MPCOptimizationParameter optimizationParameters) {
    this.optimizationParameters = optimizationParameters;
  }
}
