// code by jph
package ch.ethz.idsc.retina.lcm.lidar;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.retina.dev.urg04lxug01.Urg04lxDevice;
import ch.ethz.idsc.retina.dev.urg04lxug01.Urg04lxEvent;
import ch.ethz.idsc.retina.dev.urg04lxug01.Urg04lxEventListener;
import ch.ethz.idsc.retina.lcm.BinaryBlobPublisher;

/** encodes Urg04lxContainer to byte packet and publishes the packet via lcm */
public class Urg04lxLcmServer implements Urg04lxEventListener {
  public static final int RANGES = 682;
  // ---
  private final BinaryBlobPublisher publisher;
  private final byte[] packet = new byte[8 + RANGES * 2];

  public Urg04lxLcmServer(String lidarId) {
    publisher = new BinaryBlobPublisher(Urg04lxDevice.channel(lidarId));
  }

  @Override
  public void range(Urg04lxEvent urg04lxEvent) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(packet);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    byteBuffer.putLong(urg04lxEvent.timestamp);
    for (int count = 0; count < urg04lxEvent.range.length; ++count)
      byteBuffer.putShort(urg04lxEvent.range[count]);
    publisher.accept(packet, packet.length);
  }
}
