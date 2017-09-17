// code by jph
package ch.ethz.idsc.retina.dev.joystick;

import java.util.function.Supplier;

import ch.ethz.idsc.retina.util.GlobalAssert;

/** DO NOT INSERT NEW JOYSTICKS BETWEEN EXISTING JOYSTICKS
 * INSTEAD, APPEND NEW JOYSTICKS TO LIST */
public enum JoystickType {
  GENERIC_XBOX_PAD(() -> new GenericXboxPadJoystick(), 6, 10, 1), //
  // <- append next joystick here
  ;
  public final Supplier<JoystickEvent> supplier;
  public final int axes;
  public final int buttons;
  public final int hats;

  private JoystickType(Supplier<JoystickEvent> supplier, int axes, int buttons, int hats) {
    this.supplier = supplier;
    this.axes = axes;
    this.buttons = buttons;
    this.hats = hats;
    /** a short mask is used to encode button status */
    GlobalAssert.that(buttons <= 16);
  }

  public int encodingSize() {
    return 1 + axes + 2 + hats;
  }
}
