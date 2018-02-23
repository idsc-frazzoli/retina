// code by jph
package ch.ethz.idsc.demo.jph;

import java.util.Optional;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.retina.dev.joystick.JoystickEvent;
import ch.ethz.idsc.retina.lcm.joystick.JoystickLcmClient;

public enum JoystickLcmClientDemo {
  ;
  public static void main(String[] args) throws Exception {
    JoystickLcmClient joystickLcmClient = new JoystickLcmClient(GokartLcmChannel.JOYSTICK);
    joystickLcmClient.startSubscriptions();
    for (int index = 0; index < 50; ++index) {
      Optional<JoystickEvent> optional = joystickLcmClient.getJoystick();
      if (optional.isPresent())
        System.out.println(optional.get());
      Thread.sleep(250);
    }
    joystickLcmClient.stopSubscriptions();
    System.out.println("end");
  }
}
