// code by jph
package ch.ethz.idsc.retina.davis.io;

public enum DavisDefaultExportControl implements DavisExportControl {
  INSTANCE;
  // ---
  @Override
  public boolean isActive() {
    return true;
  }

  @Override
  public int mapTime(int time) {
    return time;
  }
}
