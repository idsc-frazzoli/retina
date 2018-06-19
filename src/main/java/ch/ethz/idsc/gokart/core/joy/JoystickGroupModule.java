// code by jph
package ch.ethz.idsc.gokart.core.joy;

import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.retina.sys.GroupModule;

/** group of modules that constitute the default joystick control of the gokart */
public class JoystickGroupModule extends GroupModule {
  @Override
  protected List<Class<?>> modules() {
    return Arrays.asList( //
        LinmotJoystickModule.class, //
        SteerJoystickModule.class, //
        RimoTorqueJoystickModule.class);
  }
}
