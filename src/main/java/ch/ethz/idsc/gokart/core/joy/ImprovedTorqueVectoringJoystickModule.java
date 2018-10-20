// code by mh
package ch.ethz.idsc.gokart.core.joy;

public class ImprovedTorqueVectoringJoystickModule extends TorqueVectoringJoystickModule {
  public ImprovedTorqueVectoringJoystickModule() {
    super(new ImprovedTorqueVectoring(TorqueVectoringConfig.GLOBAL));
  }
}
