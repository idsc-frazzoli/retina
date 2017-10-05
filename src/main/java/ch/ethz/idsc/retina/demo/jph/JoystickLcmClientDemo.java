// code by jph
package ch.ethz.idsc.retina.demo.jph;

import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.lcm.joystick.JoystickLcmClient;

public enum JoystickLcmClientDemo {
  ;
  public static void main(String[] args) throws Exception {
    JoystickLcmClient joystickLcmClient = JoystickLcmClient.INSTANCE;
    joystickLcmClient.addListener(joystickEvent -> {
      System.out.println(joystickEvent.toInfoString());
      GokartJoystickInterface gokartJoystickInterface = (GokartJoystickInterface) joystickEvent;
      // System.out.print("L-knob R=" +
      // genericXboxPadJoystick.getLeftKnobDirectionRight() + " ");
      // System.out.print("slider L=" +
      // genericXboxPadJoystick.getLeftSliderUnitValue() + " ");
      // System.out.print("button A=" + genericXboxPadJoystick.getLeftSliderUnitValue() + " ");
      // System.out.println();
    });
    joystickLcmClient.startSubscriptions();
    Thread.sleep(10000);
  }
}
