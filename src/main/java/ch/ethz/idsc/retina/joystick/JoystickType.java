// code by jph
package ch.ethz.idsc.retina.joystick;

import java.util.function.Supplier;

import ch.ethz.idsc.owl.data.GlobalAssert;

/** DO NOT INSERT NEW JOYSTICKS BETWEEN EXISTING JOYSTICKS INSTEAD APPEND NEW
 * JOYSTICKS TO LIST */
public enum JoystickType {
  /** 20180709 ante: ubuntu 17.04 recognizes joystick as generic xbox pad
   * tested on GUID=030000004c0e00001035000070000000 */
  GENERIC_XBOX_PAD( //
      () -> new GenericXboxPadJoystick(9.5), 6, 10, 1), //
  /** 20180709 post: ubuntu 18.04 recognizes joystick as Radica Gamester
   * which is coincides with the label printed on the back */
  RADICA_GAMESTER( //
      () -> new GenericXboxPadJoystick(9.5), 6, 10, 1), //
  /** tested on GUID=03000000380700005032000011010000 */
  // MAD_CATZ_FIGHTPAD_PRO_PS3( //
  // () -> new MadCatzIncMadCatzFightpadProPs3(), 4, 16, 1)
  // /** tested on GUID=030000006d0400001fc2000005030000 */
  // LOGITECH_GAMEPAD_F710( //
  // () -> new LogitechGamepadF710(), 6, 11, 1), //
  // /** tested on GUID=03000000bd1200002fa0000010010000 */
  // HAMA_5AXIS12BUTTON_WITH_POV(() -> new Hama5Axis12ButtonWithPov(), 4, 12, 1),
  // //
  /** reported to not be reliable! */
  // PC_PS3_ANDROID( //
  // () -> new PCPS3AndroidJoystick(), 4, 15, 1), //
  // <- append next joystick here
  ;
  // ---
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
