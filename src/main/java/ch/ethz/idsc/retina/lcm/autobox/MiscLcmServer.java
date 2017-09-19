// code by jph
package ch.ethz.idsc.retina.lcm.autobox;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.retina.dev.misc.MiscGetEvent;
import ch.ethz.idsc.retina.dev.misc.MiscGetListener;
import ch.ethz.idsc.retina.dev.steer.SteerGetEvent;
import ch.ethz.idsc.retina.lcm.BinaryBlobPublisher;

public enum MiscLcmServer implements MiscGetListener {
  INSTANCE;
  // ---
  private final BinaryBlobPublisher publisher = new BinaryBlobPublisher("autobox.misc.get");
  private final byte[] data = new byte[SteerGetEvent.LENGTH];

  @Override
  public void miscGet(MiscGetEvent miscGetEvent) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(data);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    miscGetEvent.encode(byteBuffer);
    publisher.accept(data, data.length);
  }
}
