// code by mh
package ch.ethz.idsc.gokart.core.joy;

public class SimpleTorqueVectoringJoystickModule extends TorqueVectoringJoystickModule {
  public SimpleTorqueVectoringJoystickModule() {
    super(new SimpleTorqueVectoring(TorqueVectoringConfig.GLOBAL));
  }
}
