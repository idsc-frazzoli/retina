// code by jph
package ch.ethz.idsc.gokart.core.man;

import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.retina.util.sys.GroupModule;

/** group of modules that constitute the default joystick control of the gokart */
public class ManualGroupModule extends GroupModule {
  @Override
  protected List<Class<?>> modules() {
    return Arrays.asList( //
        LinmotManualModule.class, //
        SteerManualModule.class, //
        RimoTorqueManualModule.class);
  }
}
