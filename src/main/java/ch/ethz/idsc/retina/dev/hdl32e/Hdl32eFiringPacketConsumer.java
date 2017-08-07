// code by jph
package ch.ethz.idsc.retina.dev.hdl32e;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.util.GlobalAssert;

/** access to a single firing packet containing
 * rotational angle, range, intensity, etc. */
public final class Hdl32eFiringPacketConsumer {
  private final List<FiringPacketInterface> firingPacketInterfaces = new LinkedList<>();

  public void addListener(FiringPacketInterface firingPacketInterface) {
    firingPacketInterfaces.add(firingPacketInterface);
  }

  /** @param byteBuffer with at least 1206 bytes to read */
  public void lasers(ByteBuffer byteBuffer) {
    { // 12 blocks of firing data
      final int offset = byteBuffer.position();
      for (int firing = 0; firing < 12; ++firing) {
        GlobalAssert.that(byteBuffer.position() == offset + firing * 100);
        @SuppressWarnings("unused")
        int blockId = byteBuffer.getShort() & 0xffff; // laser block ID, 61183 ?
        int rotational = byteBuffer.getShort() & 0xffff; // rotational [0, ..., 35999]
        // ---
        final int position = byteBuffer.position();
        final int ffiring = firing;
        firingPacketInterfaces.forEach(listener -> {
          byteBuffer.position(position);
          listener.process(ffiring, rotational, byteBuffer);
        });
        byteBuffer.position(position + 96);
      }
      // _assert(byteBuffer.position() == offset + 1206 1242); // FIXME
    }
    { // status data
      int gps_timestamp = byteBuffer.getInt();
      // System.out.println("gps=" + gps_timestamp);
      byte type = byteBuffer.get(); // 55
      byte value = byteBuffer.get(); // 33
      firingPacketInterfaces.forEach(listener -> listener.status(gps_timestamp, type, value));
    }
  }
}
