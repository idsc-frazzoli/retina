// code by jph
package ch.ethz.idsc.retina.davis.io;

// TODO rename
public enum DefaultExportControl implements ExportControl {
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
