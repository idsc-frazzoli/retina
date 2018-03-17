// code by jph
package ch.ethz.idsc.retina.dev.davis.io;

import java.nio.ByteBuffer;

/** image from camera */
public class Aedat31PolarityEvent {
  private static final int MASK15 = 0x7fff;
  // ---
  public final int x;
  public final int y;
  public final int i;
  private final int valid;
  private final int time;

  public Aedat31PolarityEvent(ByteBuffer byteBuffer) {
    final int value = byteBuffer.getInt();
    // System.out.println(String.format("v=%08x", value&0xfffffffe));
    valid = value & 1;
    i = (value >> 1) & 1;
    y = (value >> 2) & MASK15;
    x = (value >> 17) & MASK15;
    // ---
    time = byteBuffer.getInt();
  }

  @Override
  public String toString() {
    return String.format("v=%d p=%d (%4d,%4d) t=%d", valid, i, x, y, time);
  }

  public int getTime_us() {
    return time;
  }

  public boolean isValid() {
    return valid == 1;
  }
}
