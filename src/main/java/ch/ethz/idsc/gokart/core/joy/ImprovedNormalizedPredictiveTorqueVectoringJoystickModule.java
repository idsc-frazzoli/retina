// code by mh
package ch.ethz.idsc.gokart.core.joy;

public final class ImprovedNormalizedPredictiveTorqueVectoringJoystickModule extends TorqueVectoringJoystickModule {
  public ImprovedNormalizedPredictiveTorqueVectoringJoystickModule() {
    super(new ImprovedNormalizedPredictiveTorqueVectoring(TorqueVectoringConfig.GLOBAL));
  }
}
