// code by jph
package ch.ethz.idsc.retina.util.io;

import java.nio.ByteBuffer;

public class CanFrame {
  public final short id;
  private final byte[] data;

  public CanFrame(ByteBuffer byteBuffer) {
    id = byteBuffer.getShort();
    data = new byte[byteBuffer.remaining()];
    byteBuffer.get(data);
  }

  public int length() {
    return data.length;
  }

  public byte get(int index) {
    return data[index];
  }
}
