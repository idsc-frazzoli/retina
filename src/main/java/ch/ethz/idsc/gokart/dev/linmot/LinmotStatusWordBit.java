// code by jph
package ch.ethz.idsc.gokart.dev.linmot;

import java.util.EnumSet;
import java.util.Set;

/** NTI AG / LinMot, User Manual Motion Control SW / 07.04.2017
 * Page 20/132, Section 3.24 Status Word */
public enum LinmotStatusWordBit {
  /** State Nr 8 or higher (copied to Controller EN LED ) */
  OPERATION_ENABLED, // 0
  /** Control Word Bit 0 */
  SWITCH_ON, // 1
  /** Control Word Bit 3 */
  OPERATION, // 2
  /** Acknowledge with Control word Bit 7 (Reset Error) */
  ERROR, // 3
  /** Control Word Bit 1 */
  VOLTAGE, // 4
  /** Control Word Bit 2 */
  QUICK_STEP, // 5
  /** Release with 0 of Control word bit 0 (Switch On) */
  SWITCH_ON_LOCK, // 6
  /** One or more bits in the Warn Word are set */
  WARNING, // 7
  /** Event Handler setup */
  EVENT_HANDLER, // 8
  /** Special motion commands (Homing, ..) runs */
  SPECIAL_COMMAND, // 9
  /** Actual position after motion in window */
  IN_POSITION, // 10
  /** Position sensor system valid */
  HOMED, // 11
  /** A fatal error can not be acknowledged! */
  FATAL_ERROR, // 12
  /** Setpoint generation (VAI, curve) active */
  MOTION_ACTIVE, // 13
  /** Defined UPID is in Range 1 */
  IN_RANGE1, // 14
  /** Defined UPID is in Range 2 */
  IN_RANGE2, // 15
  ;
  // ---
  /** @param status_word of {@link LinmotGetEvent}
   * @return */
  public static Set<LinmotStatusWordBit> from(short status_word) {
    Set<LinmotStatusWordBit> set = EnumSet.noneOf(LinmotStatusWordBit.class);
    for (LinmotStatusWordBit linmotStatusWordBit : values()) {
      boolean selected = (status_word & (1 << linmotStatusWordBit.ordinal())) != 0;
      if (selected)
        set.add(linmotStatusWordBit);
    }
    return set;
  }
}
