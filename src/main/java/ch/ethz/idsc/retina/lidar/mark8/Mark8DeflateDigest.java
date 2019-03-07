// code by jph
package ch.ethz.idsc.retina.lidar.mark8;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.owl.data.GlobalAssert;

public class Mark8DeflateDigest implements Mark8Digest {
  private static final int ENCODING_MAX = 65535; // 65535
  private static final int FIRING_SIZE = 132;
  private boolean notify = true;
  private final byte[] array;
  private final byte[] intensity;

  /** @param returns
   * 1, 2, or 3 */
  public Mark8DeflateDigest(int returns) {
    GlobalAssert.that(0 < returns && returns <= 3);
    intensity = new byte[8 * returns];
    array = new byte[8 + Mark8Device.FIRINGS * (2 + 3 * intensity.length)];
  }

  @Override
  public byte[] digest(byte[] data) {
    ByteBuffer src = ByteBuffer.wrap(data);
    src.order(ByteOrder.BIG_ENDIAN); // mark8
    ByteBuffer dst = ByteBuffer.wrap(array);
    dst.order(ByteOrder.LITTLE_ENDIAN); // retina standard
    // timestamp
    src.position(8); // drop: header and length
    dst.putInt(src.getInt()); // sec
    dst.putInt(src.getInt()); // nano sec
    // firing data
    for (int count = 0; count < Mark8Device.FIRINGS; ++count) {
      int offset = 20 + count * FIRING_SIZE; // TODO multiplication not necessary
      src.position(offset + 100);
      src.get(intensity);
      src.position(offset);
      dst.putShort(src.getShort()); // rotational
      src.getShort(); // drop: reserved
      for (int laser = 0; laser < intensity.length; ++laser) {
        int dist = src.getInt();
        dist /= 200; // 100000 to 500
        if (ENCODING_MAX < dist) {
          if (notify) {
            System.err.println("distance encoding fail=" + dist);
            notify = false;
          }
          dist = 0;
        }
        dst.putShort((short) dist);
        dst.put(intensity[laser]);
      }
    }
    src.position(20 + Mark8Device.FIRINGS * FIRING_SIZE); // drop: header and length
    src.getInt(); // sec
    src.getInt(); // nano sec
    src.getInt();
    // ---
    GlobalAssert.that(dst.remaining() == 0);
    GlobalAssert.that(src.remaining() == 0);
    return array;
  }
}
