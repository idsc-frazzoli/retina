// code by jph
package ch.ethz.idsc.retina.davis._240c;

import ch.ethz.idsc.retina.davis.DavisDecoder;
import ch.ethz.idsc.retina.davis.DavisDevice;

/** Specifications and considerations:
 * 
 * 240 x 180 == 43200 pixels the raw data encodes each pixel in 8 bytes 43200 x
 * 8 == 345600 bytes
 * 
 * there are ~20 frames per seconds 345600 x 20 == 6912000 bytes / seconds == ~7
 * MB/sec */
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

  @Override
  public DavisDecoder createDecoder() {
    return new Davis240cDecoder();
  }
}
