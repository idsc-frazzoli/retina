// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import javax.swing.WindowConstants;

public class JoystickSimpleDriveModule extends JoystickAbstractModule {
  @Override
  protected HmiAbstractJoystick createJoystick() {
    return new HmiSimpleDriveJoystick();
  }

  public static void standalone() throws Exception {
    JoystickSimpleDriveModule autoboxTestingModule = new JoystickSimpleDriveModule();
    autoboxTestingModule.first();
    autoboxTestingModule.jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }

  public static void main(String[] args) throws Exception {
    standalone();
  }
}
