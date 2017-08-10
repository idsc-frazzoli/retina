// code by jph
package ch.ethz.idsc.retina.davis._240c;

/* package */ class ApsTracker {
  private int lastIndex = -1;
  private boolean complete = true;

  public void aps(ApsDavisEvent apsDavisEvent, int height) {
    int refIndex = apsDavisEvent.y + apsDavisEvent.x * height;
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
