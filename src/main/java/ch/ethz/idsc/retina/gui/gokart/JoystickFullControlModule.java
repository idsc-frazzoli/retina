// code by jph
package ch.ethz.idsc.retina.gui.gokart;

public class JoystickFullControlModule extends JoystickAbstractModule {
  @Override
  protected HmiAbstractJoystick createJoystick() {
    return new HmiFullControlJoystick();
  }
}
