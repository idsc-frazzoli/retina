// code by mh
package ch.ethz.idsc.gokart.core.joy;

public class SimpleTorqueVectoringJoystickModule extends TorqueVectoringJoystickModule {
  SimpleTorqueVectoringJoystickModule() {
    super(new SimpleTorqueVectoring(TorqueVectoringConfig.GLOBAL));
  }
}
