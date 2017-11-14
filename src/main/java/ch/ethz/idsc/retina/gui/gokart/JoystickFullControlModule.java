// code by jph
package ch.ethz.idsc.retina.gui.gokart;

public class JoystickFullControlModule extends JoystickAbstractModule {
  @Override
  protected HmiAbstractJoystick createJoystick() {
    return new HmiFullControlJoystick();
  }

  public static void main(String[] args) throws Exception {
    new JoystickFullControlModule().first();
  }
}
