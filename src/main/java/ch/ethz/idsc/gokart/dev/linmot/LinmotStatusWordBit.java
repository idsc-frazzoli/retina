// code by jph
package ch.ethz.idsc.gokart.dev.linmot;

import java.util.EnumSet;
import java.util.Set;

/** NTI AG / LinMot, User Manual Motion Control SW / 07.04.2017
 * Page 20/132, Section 3.24 Status Word */
public enum LinmotStatusWordBit {
  OPERATION_ENABLED, // 0
  SWITCH_ON, // 1
  OPERATION, // 2
  ERROR, // 3
  VOLTAGE, // 4
  QUICK_STEP, // 5
  SWITCH_ON_LOCK, // 6
  WARNING, // 7
  EVENT_HANDLER, // 8
  SPECIAL_COMMAND, // 9
  IN_POSITION, // 10
  HOMED, // 11
  FATAL_ERROR, // 12
  MOTION_ACTIVE, // 13
  IN_RANGE1, // 14
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
