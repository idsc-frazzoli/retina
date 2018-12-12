// code by mh
package ch.ethz.idsc.gokart.core.joy;

public final class ImprovedTorqueVectoringJoystickModule extends TorqueVectoringJoystickModule {
  public ImprovedTorqueVectoringJoystickModule() {
    super(new ImprovedTorqueVectoring(TorqueVectoringConfig.GLOBAL));
  }
}
