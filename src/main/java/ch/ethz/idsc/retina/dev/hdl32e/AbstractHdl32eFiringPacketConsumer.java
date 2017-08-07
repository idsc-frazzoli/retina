// code by jph
package ch.ethz.idsc.retina.dev.hdl32e;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.GlobalAssert;

// TODO DO NOT extend from here but pass data to listeners
public abstract class AbstractHdl32eFiringPacketConsumer implements Hdl32eFiringPacketConsumer {
  public static final int LASERS = 32;

  @SuppressWarnings("unused")
  @Override
  public final void lasers(ByteBuffer byteBuffer) {
    {
      final int offset = byteBuffer.position();
      // 12 blocks of firing data
      for (int firing = 0; firing < 12; ++firing) {
        GlobalAssert.that(byteBuffer.position() == offset + firing * 100);
        int blockId = byteBuffer.getShort() & 0xffff; // laser block ID, 61183 ?
        int rotational = byteBuffer.getShort() & 0xffff; // rotational [0, ..., 35999]
        // ---
        process(firing, rotational, byteBuffer);
      }
      // _assert(byteBuffer.position() == offset + 1206 1242); // FIXME
    }
    { // status data
      int gps_timestamp = byteBuffer.getInt();
      // System.out.println("gps=" + gps_timestamp);
      byte type = byteBuffer.get(); // 55
      byte value = byteBuffer.get(); // 33
      status(gps_timestamp, type, value);
    }
  }

  /** implementations have to advance byteBuffer by 96 bytes:
   * 
   * for (int laser = 0; laser < LASERS; ++laser) {
   * int distance = byteBuffer.getShort() & 0xffff;
   * int intensity = byteBuffer.get();
   * }
   * 
   * @param firing ranges from [0, ..., 11]
   * @param rotational [0, ..., 35999]
   * @param byteBuffer */
  public abstract void process(int firing, int rotational, ByteBuffer byteBuffer);

  public abstract void status(int usec, byte type, byte value);
}
