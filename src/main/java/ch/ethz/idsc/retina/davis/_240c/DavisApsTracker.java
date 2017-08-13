// code by jph
package ch.ethz.idsc.retina.davis._240c;

/* package */ class DavisApsTracker {
  private int lastIndex = -1;
  private boolean complete = true;

  public void aps(DavisApsEvent davisApsEvent, int height) {
    int refIndex = davisApsEvent.y + davisApsEvent.x * height;
    complete &= lastIndex + 1 == refIndex;
    lastIndex = refIndex;
  }

  boolean statusAndReset() {
    boolean value = complete;
    complete = true;
    lastIndex = -1;
    return value;
  }
}
