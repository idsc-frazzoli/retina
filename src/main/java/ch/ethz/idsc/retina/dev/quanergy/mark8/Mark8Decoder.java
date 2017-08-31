// code by jph
package ch.ethz.idsc.retina.dev.quanergy.mark8;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.dev.velodyne.LidarRayDataListener;
import ch.ethz.idsc.retina.lcm.lidar.Mark8LcmClient;

/** Packet description taken from
 * M8 Sensor User Guide, QPN 96-00001 Rev H
 * p.32 */
public class Mark8Decoder {
  private static final int FIRINGS = 50;
  /** ordering taken from p.33 */
  public static final int[] ORDERING = new int[] { 0, 4, 2, 6, 1, 5, 3, 7 };
  /** angles taken from p.33 */
  // TODO the values state in the data sheet are not evenly spaced... need confirmation through experiments
  public static final double[] M8_VERTICAL_ANGLES = { //
      -0.318505, -0.2692, -0.218009, -0.165195, -0.111003, -0.0557982, 0.0, 0.0557982 };
  // ---
  private final List<LidarRayDataListener> listeners = new LinkedList<>();

  public void addRayListener(LidarRayDataListener lidarRayDataListener) {
    listeners.add(lidarRayDataListener);
  }

  public void lasers(ByteBuffer byteBuffer) {
    final int header = byteBuffer.getInt();
    final int length = byteBuffer.getInt();
    if (header != Mark8Device.HEADER || length != Mark8Device.LENGTH)
      throw new RuntimeException();
    // ---
    int timestamp_seconds = byteBuffer.getInt();
    int timestamp_nanos = byteBuffer.getInt(); // 31 bit == 2147483648
    int usec = timestamp_seconds * 1_000_000 + timestamp_nanos / 1000;
    listeners.forEach(listener -> listener.timestamp(usec, (byte) 0));
    // System.out.println(timestamp_seconds + " " + timestamp_nanos);
    byteBuffer.get(); // api_version_major
    byteBuffer.get(); // api_version_minor
    byteBuffer.get(); // api_version_patch
    byte packet_type = byteBuffer.get();
    // our quanergy only sends packet-type 0x00
    if (packet_type != 0)
      throw new RuntimeException();
    // READ FIRING DATA [50]
    for (int index = 0; index < FIRINGS; ++index) {
      /** rotation [0, ..., 10399] */
      final int rotational = byteBuffer.getShort();
      byteBuffer.getShort(); // reserved, don't use
      final int position = byteBuffer.position();
      listeners.forEach(listener -> {
        byteBuffer.position(position);
        listener.scan(rotational, byteBuffer);
      });
      byteBuffer.position(position + 128); // 24 * 4 + 24 + 8
      // for (int count = 0; count < 24; ++count) {
      // // 0 indicates an invalid point
      // byteBuffer.getInt(); // distances in 10 micrometers
      // }
      // for (int count = 0; count < 24; ++count) {
      // int intensity = byteBuffer.get() & 0xff; // intensity
      // }
      // long status = byteBuffer.getLong(); // status
      // if (status != 0)
      // throw new RuntimeException();
    }
    byteBuffer.getInt(); // timestamp seconds
    byteBuffer.getInt(); // timestamp nanos
    byteBuffer.getShort(); // API version
    byteBuffer.getShort(); // status
    // System.out.println(byteBuffer.remaining());
  }

  public static void main(String[] args) throws Exception {
    Mark8LcmClient mark8LcmClient = new Mark8LcmClient(new Mark8Decoder(), "center");
    mark8LcmClient.startSubscriptions();
    Thread.sleep(4000);
  }
}
