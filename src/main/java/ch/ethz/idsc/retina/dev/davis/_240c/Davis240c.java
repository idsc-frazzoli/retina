// code by jph
package ch.ethz.idsc.retina.dev.davis._240c;

import ch.ethz.idsc.retina.dev.davis.DavisDecoder;
import ch.ethz.idsc.retina.dev.davis.DavisDevice;

public enum Davis240c implements DavisDevice {
  INSTANCE;
  // ---
  private static final int WIDTH = 240;
  private static final int HEIGHT = 180;

  @Override
  public int getWidth() {
    return WIDTH;
  }

  @Override
  public int getHeight() {
    return HEIGHT;
  }

  public DavisDecoder createDecoder() {
    return new Davis240cDecoder();
  }
}
