// code by jph
package ch.ethz.idsc.retina.dev.lidar.mark8;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.retina.util.GlobalAssert;

public class Mark8DeflateDigest implements Mark8Digest {
  public static final int LENGTH = 3708;
  private static final int FIRING_SIZE = 132;
  private final byte[] array = new byte[LENGTH]; // TODO

  @Override
  public byte[] digest(byte[] data) {
    ByteBuffer src = ByteBuffer.wrap(data);
    src.order(ByteOrder.BIG_ENDIAN); // mark8
    ByteBuffer dst = ByteBuffer.wrap(array);
    dst.order(ByteOrder.LITTLE_ENDIAN); // retina standard
    // header
    // dst.putShort(Mark8Device.DEFLAT);
    // timestamp
    src.position(8); // drop: header and length
    dst.putInt(src.getInt()); // sec
    dst.putInt(src.getInt()); // nano sec
    // firing data
    byte[] intensity = new byte[24];
    for (int count = 0; count < 50; ++count) {
      int offset = 20 + count * FIRING_SIZE;
      src.position(offset + 100);
      src.get(intensity);
      src.position(offset);
      dst.putShort(src.getShort()); // rotational
      src.getShort(); // drop: reserved
      for (int laser = 0; laser < 24; ++laser) {
        int dist = src.getInt();
        dist /= 200; // 100000 to 500
        if (65535 < dist) {
          System.out.println("distenc=" + dist);
        }
        // TODO assert that fits into 16 bit otherwise 0
        dst.putShort((short) dist);
        dst.put(intensity[laser]);
      }
    }
    src.position(20 + 50 * 132); // drop: header and length
    src.getInt(); // sec
    src.getInt(); // nano sec
    src.getInt();
    // ---
    GlobalAssert.that(dst.remaining() == 0);
    GlobalAssert.that(src.remaining() == 0);
    return array;
  }
}
