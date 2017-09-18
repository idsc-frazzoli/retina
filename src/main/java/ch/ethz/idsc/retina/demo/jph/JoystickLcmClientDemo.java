// code by jph
package ch.ethz.idsc.retina.demo.jph;

import ch.ethz.idsc.retina.dev.joystick.GenericXboxPadJoystick;
import ch.ethz.idsc.retina.dev.joystick.JoystickType;
import ch.ethz.idsc.retina.lcm.joystick.JoystickLcmClient;

public enum JoystickLcmClientDemo {
  ;
  public static void main(String[] args) throws Exception {
    JoystickLcmClient joystickLcmClient = new JoystickLcmClient(JoystickType.GENERIC_XBOX_PAD);
    joystickLcmClient.addListener(joystickEvent -> {
      System.out.println(joystickEvent.toInfoString());
      GenericXboxPadJoystick genericXboxPadJoystick = (GenericXboxPadJoystick) joystickEvent;
      // System.out.print("L-knob R=" +
      // genericXboxPadJoystick.getLeftKnobDirectionRight() + " ");
      // System.out.print("slider L=" +
      // genericXboxPadJoystick.getLeftSliderUnitValue() + " ");
      System.out.print("button A=" + genericXboxPadJoystick.isButtonPressedA() + " ");
      System.out.println();
    });
    joystickLcmClient.startSubscriptions();
    Thread.sleep(10000);
  }
}
