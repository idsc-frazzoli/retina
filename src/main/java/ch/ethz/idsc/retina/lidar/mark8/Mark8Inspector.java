// code by jph
package ch.ethz.idsc.retina.lidar.mark8;

import java.nio.ByteBuffer;
import java.util.Arrays;

import ch.ethz.idsc.retina.lidar.LidarRayDataListener;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class Mark8Inspector implements LidarRayDataListener {
  private final byte[] block0 = new byte[3];
  private final byte[] block1 = new byte[3];
  private final byte[] block2 = new byte[3];
  private int returns;

  @Override
  public void timestamp(int usec, int type) {
    returns = type;
  }

  @Override
  public void scan(int rotational, ByteBuffer byteBuffer) {
    // System.out.println(rotational);
    // if (rotational == 13) {
    // System.out.println("---- " + rotational);
    // apparently the correct nesting of loops (test more)
    int position = byteBuffer.position();
    for (int laser = 0; laser < 8; ++laser) {
      byteBuffer.position(position + laser * 3);
      byteBuffer.get(block0);
      if (1 < returns) {
        byteBuffer.position(position + laser * 3 + 8 * 3);
        byteBuffer.get(block1);
      }
      if (2 < returns) {
        byteBuffer.position(position + laser * 3 + 8 * 6);
        byteBuffer.get(block2);
      }
      boolean s1 = Arrays.equals(block0, block1);
      boolean s2 = Arrays.equals(block0, block2);
      if (s1 && s2) {
        // ---
      } else {
        // System.out.println("================");
        // System.out.println("rotational " + rotational);
        // Tensor matrix = Array.zeros(8, 3 * 2);
        // for (int ret = 0; ret < 3; ++ret)
        ByteBuffer b0 = ByteBuffer.wrap(block0);
        ByteBuffer b1 = ByteBuffer.wrap(block1);
        ByteBuffer b2 = ByteBuffer.wrap(block2);
        int d0 = b0.getShort() & 0xffff;
        int d1 = b1.getShort() & 0xffff;
        int d2 = b2.getShort() & 0xffff;
        int i0 = b0.get() & 0xff;
        int i1 = b1.get() & 0xff;
        int i2 = b2.get() & 0xff;
        Tensor r = Tensors.vector(rotational, laser, d0, d1, d2, i0, i1, i2);
        r.length(); // eliminate unused warning
        // System.out.println(Pretty.of(r));
      }
    }
  }
}
