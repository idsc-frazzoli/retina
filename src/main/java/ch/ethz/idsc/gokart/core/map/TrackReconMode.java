// code by mh, jph
package ch.ethz.idsc.gokart.core.map;

public enum TrackReconMode {
  ACTIVE_SEND_LATEST(true, true), //
  /** does not update track and sends last computed track */
  PASSIVE_SEND_LAST(true, false), //
  /** does not update track and sends empty track */
  PASSIVE_SEND_EMPTY(false, false), //
  ;
  // ---
  private final boolean isSendLast;
  private final boolean isActive;

  private TrackReconMode(boolean isSendLast, boolean isActive) {
    this.isSendLast = isSendLast;
    this.isActive = isActive;
  }

  boolean isSendLast() {
    return isSendLast;
  }

  public boolean isActive() {
    return isActive;
  }
}
