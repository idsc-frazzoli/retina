// code by jph
package ch.ethz.idsc.retina.lcm.autobox;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.retina.dev.steer.SteerGetEvent;
import ch.ethz.idsc.retina.dev.steer.SteerGetListener;
import ch.ethz.idsc.retina.lcm.BinaryBlobPublisher;

public enum SteerLcmServer implements SteerGetListener {
  INSTANCE;
  // ---
  private final BinaryBlobPublisher publisher = new BinaryBlobPublisher("autobox.steer.get");
  private final byte[] data = new byte[SteerGetEvent.LENGTH];

  @Override
  public void getEvent(SteerGetEvent steerGetEvent) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(data);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    steerGetEvent.encode(byteBuffer);
    publisher.accept(data, data.length);
  }
}
