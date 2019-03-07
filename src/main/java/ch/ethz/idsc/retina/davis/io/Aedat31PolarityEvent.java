// code by jph
package ch.ethz.idsc.retina.davis.io;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.davis._240c.DavisDvsEvent;

/** image from camera */
public class Aedat31PolarityEvent extends DavisDvsEvent {
  private static final int MASK15 = 0x7fff;

  public static Aedat31PolarityEvent create(ByteBuffer byteBuffer) {
    final int value = byteBuffer.getInt();
    int valid = value & 1;
    int i = (value >> 1) & 1;
    int y = (value >> 2) & MASK15;
    int x = (value >> 17) & MASK15;
    int time = byteBuffer.getInt();
    return new Aedat31PolarityEvent(time, x, y, i, valid);
  }

  // ---
  private final int valid;

  public Aedat31PolarityEvent(int time, int x, int y, int i, int valid) {
    super(time, x, y, i);
    this.valid = valid;
  }

  public boolean isValid() {
    return valid == 1;
  }

  @Override
  public String toString() {
    return String.format("v=%d p=%d (%4d,%4d) t=%d", valid, i, x, y, time);
  }
}
