// code by mh
package ch.ethz.idsc.gokart.core.joy;

public class ImprovedTorqueVectoringJoystickModule extends TorqueVectoringJoystickModule {
  ImprovedTorqueVectoringJoystickModule() {
    super(new ImprovedTorqueVectoring(TorqueVectoringConfig.GLOBAL));
  }
}
