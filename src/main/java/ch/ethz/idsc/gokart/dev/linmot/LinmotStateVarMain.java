// code by jph
package ch.ethz.idsc.gokart.dev.linmot;

/** NTI AG / LinMot, User Manual Motion Control SW / 07.04.2017
 * Page 14/132
 * https://polybox.ethz.ch/index.php/s/74ZoAvDCO9sauTA */
public enum LinmotStateVarMain {
  /** Not Ready To Switch On */
  NOT_READY, //
  SWITCH_ON_DISABLED, //
  READY_TO_SWITCH_ON, //
  /** substate = Error Code which will be logged */
  SETUP_ERROR, //
  /** substate = Logged Error Code */
  ERROR, //
  HW_TESTS, //
  READY_TO_OPERATE, //
  _UNASSIGNED_, //
  OPERATION_ENABLED, //
  HOMING, //
  CLEARANCE_CHECK, //
  GOING_TO_INITIAL_POSITION, //
  ABORTING, //
  FREEZING, //
  /** Quick Stop (Error Behavior) */
  QUICK_STOP, //
  GOING_TO_POSITION, //
  JOGGING_POSITIVE, //
  JOGGING_NEGATIVE, //
  LINEARIZING, //
  PHASE_SEARCH, //
  SPECIAL_MODE, //
  BRAKE_DELAY, //
  ;
}
