// code by jph
package ch.ethz.idsc.retina.gui.gokart;

public class JoystickSimpleDriveModule extends JoystickAbstractModule {
  @Override
  protected HmiAbstractJoystick createJoystick() {
    return new HmiSimpleDriveJoystick();
  }

  public static void main(String[] args) throws Exception {
    new JoystickSimpleDriveModule().first();
  }
}
