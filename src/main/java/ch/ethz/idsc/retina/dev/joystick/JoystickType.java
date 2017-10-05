// code by jph
package ch.ethz.idsc.retina.dev.joystick;

import java.util.function.Supplier;

import ch.ethz.idsc.retina.util.GlobalAssert;

/** DO NOT INSERT NEW JOYSTICKS BETWEEN EXISTING JOYSTICKS INSTEAD, APPEND NEW
 * JOYSTICKS TO LIST */
public enum JoystickType {
  GENERIC_XBOX_PAD( //
      () -> new GenericXboxPadJoystick(), 6, 10, 1), //
  /** tested on GUID=030000006d0400001fc2000005030000 */
  LOGITECH_GAMEPAD_F710( //
      () -> new LogitechGamepadF710(), 6, 11, 1), //
  /** reported to not be reliable! */
  // PC_PS3_ANDROID( //
  // () -> new PCPS3AndroidJoystick(), 4, 15, 1), //
  // /** tested on GUID=030000006d04000019c2000011010000 */
  // LOGITECH_LOGITECH_CORDLESS_RUMBLEPAD_2( //
  // () -> new LogitechLogitechCordlessRumblepad2(), 4, 16, 1), //
  // <- append next joystick here
  ;public final Supplier<JoystickEvent> supplier;
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
