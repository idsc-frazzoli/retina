// code by rvmoos and jph
package ch.ethz.idsc.gokart.dev.misc;

public enum MiscEmergencyBit {
  /** flag set by micro-autobox to indicate communication timeout */
  COMM_TIMEOUT, //
  /** not yet implemented: in the future, stop signal by driver or other mechanism */
  MANUAL_SWITCH, //
  ;
  private final int MASK;

  private MiscEmergencyBit() {
    MASK = 1 << ordinal();
  }

  /** @param emergency
   * @return true if given emergency bit mask has this bit set */
  public boolean isActive(byte emergency) {
    return (emergency & MASK) == MASK;
  }
}
