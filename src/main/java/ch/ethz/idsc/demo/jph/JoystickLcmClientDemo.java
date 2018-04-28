// code by jph
package ch.ethz.idsc.demo.jph;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.joy.JoystickConfig;
import ch.ethz.idsc.retina.dev.joystick.JoystickEvent;
import ch.ethz.idsc.retina.lcm.joystick.JoystickLcmProvider;

/** display joystick status in console */
enum JoystickLcmClientDemo {
  ;
  public static void main(String[] args) throws Exception {
    JoystickLcmProvider joystickLcmProvider = JoystickConfig.GLOBAL.createProvider();
    joystickLcmProvider.startSubscriptions();
    for (int index = 0; index < 50; ++index) {
      Optional<JoystickEvent> optional = joystickLcmProvider.getJoystick();
      System.out.println(optional.isPresent() ? optional.get() : "no joystick");
      Thread.sleep(250);
    }
    joystickLcmProvider.stopSubscriptions();
    System.out.println("end");
  }
}
