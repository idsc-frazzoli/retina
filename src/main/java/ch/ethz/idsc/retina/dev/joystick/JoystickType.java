// code by jph
package ch.ethz.idsc.retina.dev.joystick;

import java.util.function.Supplier;

/** DO NOT INSERT NEW JOYSTICKS BETWEEN EXISTING JOYSTICKS
 * INSTEAD, APPEND NEW JOYSTICKS TO LIST */
public enum JoystickType {
  GENERIC_XBOX_PAD(() -> new GenericXboxPadJoystick(), 6, 14, 1), //
  // <- append next joystick here
  ;
  public final Supplier<AbstractJoystick> supplier;
  public final int axes;
  public final int buttons;
  public final int hats;

  private JoystickType(Supplier<AbstractJoystick> supplier, int axes, int buttons, int hats) {
    this.supplier = supplier;
    this.axes = axes;
    this.buttons = buttons;
    this.hats = hats;
  }
}
