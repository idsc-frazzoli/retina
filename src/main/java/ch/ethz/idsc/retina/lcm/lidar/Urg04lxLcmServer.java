// code by jph
package ch.ethz.idsc.retina.lcm.lidar;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.retina.dev.urg04lxug01.Urg04lxDevice;
import ch.ethz.idsc.retina.dev.urg04lxug01.Urg04lxEvent;
import ch.ethz.idsc.retina.dev.urg04lxug01.Urg04lxListener;
import ch.ethz.idsc.retina.lcm.BinaryBlobPublisher;

/** encodes Urg04lxContainer to byte packet and publishes the packet via lcm */
public class Urg04lxLcmServer implements Urg04lxListener {
  public static final int RANGES = 682;
  // ---
  private final BinaryBlobPublisher publisher;
  private final byte[] packet = new byte[8 + RANGES * 2];

  public Urg04lxLcmServer(String lidarId) {
    publisher = new BinaryBlobPublisher(Urg04lxDevice.channel(lidarId));
  }

  @Override
  public void urg(String line) {
    Urg04lxEvent container = Urg04lxEvent.fromString(line);
    ByteBuffer byteBuffer = ByteBuffer.wrap(packet);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    byteBuffer.putLong(container.timestamp);
    for (int count = 0; count < container.range.length; ++count)
      byteBuffer.putShort(container.range[count]);
    publisher.accept(packet, packet.length);
  }
}
