// code by jph
package ch.ethz.idsc.retina.dev.velodyne;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class DemoHDL32EFiringPacketConsumer implements HDL32EFiringPacketConsumer {
  // private byte[] ethernet_header = new byte[42];
  @Override
  public void lasers(byte[] laser_data) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(laser_data);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    {
      byteBuffer.position(42); // begin of firing data
      // 12 blocks of firing data
      for (int firing = 0; firing < 12; ++firing) {
        _assert(byteBuffer.position() == 42 + firing * 100);
        int blockId = byteBuffer.getShort() & 0xffff; // laser block ID, 61183
        int rotational = byteBuffer.getShort() & 0xffff; // rotational [0, ..., 35999]
        // System.out.println(blockId+" "+rotational);
        for (int laser = 0; laser < 32; ++laser) {
          int distance = byteBuffer.getShort();
          int intensity = byteBuffer.get();
          // System.out.println(distance);
        }
      }
      _assert(byteBuffer.position() == 1242);
    }
    { // status data
      // byteBuffer.position(1242);
      int gps_timestamp = byteBuffer.getInt();
      // System.out.println(gps);
      byte type = byteBuffer.get(); // 55
      byte value = byteBuffer.get(); // 33
      // System.out.println(" " + type + " " + value);
    }
  }

  private static void _assert(boolean check) {
    if (!check)
      throw new RuntimeException();
  }
}
