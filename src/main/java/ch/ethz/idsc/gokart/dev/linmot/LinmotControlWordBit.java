// code by jph
package ch.ethz.idsc.gokart.dev.linmot;

/** NTI AG / LinMot, User Manual Motion Control SW / 07.04.2017
 * Page 19/132, Section 3.23 Control Word */
public enum LinmotControlWordBit {
  /** State change from switch on disabled to ready to switch on */
  SWITCH_ON, //
  /** Operation */
  VOLTAGE_ENABLE, //
  /** Operation */
  QUICK_STOP, //
  /** Position controller active Motion Commands enabled */
  ENABLE_OPERATION, //
  /** Operation */
  ABORT, //
  /** Rising edge will reactivate motion command */
  FREEZE, //
  /** Go to fixed parameterized Position. Wait for release of signal. */
  GO_TO_POSITION, //
  /** Rising edge of signal acknowledges error */
  ERROR_ACK, //
  /** Jog Move + */
  JOG_MOVE_POS, //
  /** Jog Move - */
  JOG_MOVE_NEG, //
  /** Special Mode */
  SPECIAL_MODE, //
  /** At startup bit 11 Status word is cleared, until procedure is finished. */
  HOME, //
  /** Enable Clearance Check Movements */
  CLEARANCE_CHECK, //
  /** Rising edge will start go to initial position */
  GO_TO_INITIAL_POSITION, //
  /** Reserved */
  RESERVED, //
  /** Enable Phase Search Movements */
  PHASE_SEARCH, //
  ;
}
