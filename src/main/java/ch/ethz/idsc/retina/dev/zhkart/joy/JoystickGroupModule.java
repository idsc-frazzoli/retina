// code by jph
package ch.ethz.idsc.retina.dev.zhkart.joy;

import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.retina.sys.GroupModule;

public class JoystickGroupModule extends GroupModule {
  @Override
  protected List<Class<?>> modules() {
    return Arrays.asList(//
        LinmotJoystickModule.class, //
        SteerJoystickModule.class, //
        RimoTorqueJoystickModule.class //
    );
  }
}
