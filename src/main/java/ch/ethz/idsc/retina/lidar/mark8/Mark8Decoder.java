// code by jph
package ch.ethz.idsc.retina.lidar.mark8;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.lidar.LidarRayDataListener;
import ch.ethz.idsc.retina.lidar.LidarRayDataProvider;

/** Packet description taken from M8 Sensor User Guide, QPN 96-00001 Rev H p.32 */
public class Mark8Decoder implements LidarRayDataProvider {
  private final List<LidarRayDataListener> listeners = new LinkedList<>();

  @Override
  public void addRayListener(LidarRayDataListener lidarRayDataListener) {
    listeners.add(lidarRayDataListener);
  }

  @Override
  public boolean hasRayListeners() {
    return !listeners.isEmpty();
  }

  public void lasers(ByteBuffer byteBuffer) {
    switch (byteBuffer.remaining()) {
    case 1308:
      lasersDeflated(byteBuffer, 1);
      break;
    case 2508:
      lasersDeflated(byteBuffer, 2);
      break;
    case 3708:
      lasersDeflated(byteBuffer, 3);
      break;
    case Mark8Device.LENGTH:
      lasersMark8(byteBuffer);
      break;
    default:
      throw new RuntimeException(Integer.toString(byteBuffer.remaining()));
    }
  }

  private static int usec(int timestamp_seconds, int timestamp_nanos) {
    return timestamp_seconds * 1_000_000 + timestamp_nanos / 1000;
  }

  private void lasersMark8(ByteBuffer byteBuffer) {
    byteBuffer.order(ByteOrder.BIG_ENDIAN);
    final int header = byteBuffer.getInt();
    final int length = byteBuffer.getInt();
    if (header != Mark8Device.HEADER || length != Mark8Device.LENGTH)
      throw new RuntimeException();
    // ---
    int timestamp_seconds = byteBuffer.getInt();
    int timestamp_nanos = byteBuffer.getInt(); // 31 bit == 2147483648
    int usec = usec(timestamp_seconds, timestamp_nanos);
    listeners.forEach(listener -> listener.timestamp(usec, 3));
    // byteBuffer.get(); // api_version_major
    // byteBuffer.get(); // api_version_minor
    // byteBuffer.get(); // api_version_patch
    // byteBuffer.get(); // packet_type, quanergy only sends packet-type 0x00
    // instead of reading 4 separate bytes, we simply read 1 integer:
    byteBuffer.getInt(); // [3 x api, packet type]
    // READ FIRING DATA [50]
    for (int index = 0; index < Mark8Device.FIRINGS; ++index) {
      /** rotation [0, ..., 10399] */
      final int rotational = byteBuffer.getShort();
      byteBuffer.getShort(); // reserved, don't use
      final int position = byteBuffer.position();
      listeners.forEach(listener -> {
        byteBuffer.position(position);
        listener.scan(rotational, byteBuffer);
      });
      byteBuffer.position(position + 128); // 24 * 4 + 24 + 8
    }
    byteBuffer.getInt(); // timestamp seconds
    byteBuffer.getInt(); // timestamp nanos
    byteBuffer.getShort(); // API version
    byteBuffer.getShort(); // status
    if (byteBuffer.remaining() != 0)
      throw new RuntimeException();
  }

  private void lasersDeflated(ByteBuffer byteBuffer, int returns) {
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    int timestamp_seconds = byteBuffer.getInt();
    int timestamp_nanos = byteBuffer.getInt(); // 31 bit == 2147483648
    int usec = usec(timestamp_seconds, timestamp_nanos);
    listeners.forEach(listener -> listener.timestamp(usec, returns));
    // READ FIRING DATA [50]
    int length = returns * 24;
    for (int index = 0; index < Mark8Device.FIRINGS; ++index) {
      /** rotation [0, ..., 10399] */
      final int rotational = byteBuffer.getShort();
      final int position = byteBuffer.position();
      listeners.forEach(listener -> {
        byteBuffer.position(position);
        listener.scan(rotational, byteBuffer);
      });
      byteBuffer.position(position + length);
    }
    if (byteBuffer.remaining() != 0)
      throw new RuntimeException();
  }
}
