// code by mh
package ch.ethz.idsc.gokart.core.joy;

public class ImprovedNormalizedTorqueVectoringJoystickModule extends TorqueVectoringJoystickModule {
  public ImprovedNormalizedTorqueVectoringJoystickModule() {
    super(new ImprovedNormalizedTorqueVectoring(TorqueVectoringConfig.GLOBAL));
  }
}
