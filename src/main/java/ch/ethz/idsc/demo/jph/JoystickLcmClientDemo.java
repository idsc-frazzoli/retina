// code by jph
package ch.ethz.idsc.demo.jph;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.joy.ManualConfig;
import ch.ethz.idsc.retina.dev.joystick.ManualControlInterface;
import ch.ethz.idsc.retina.dev.joystick.ManualControlProvider;

/** display joystick status in console */
enum JoystickLcmClientDemo {
  ;
  public static void main(String[] args) throws Exception {
    ManualControlProvider joystickLcmProvider = ManualConfig.GLOBAL.createProvider();
    joystickLcmProvider.start();
    for (int index = 0; index < 50; ++index) {
      Optional<ManualControlInterface> optional = joystickLcmProvider.getManualControl();
      System.out.println(optional.isPresent() ? optional.get() : "no joystick");
      Thread.sleep(250);
    }
    joystickLcmProvider.stop();
    System.out.println("end");
  }
}
