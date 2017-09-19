// code by jph
package ch.ethz.idsc.retina.lcm.autobox;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoGetListener;
import ch.ethz.idsc.retina.lcm.BinaryBlobPublisher;

public enum RimoLcmServer implements RimoGetListener {
  INSTANCE;
  // ---
  private final BinaryBlobPublisher publisher = new BinaryBlobPublisher("autobox.rimo.get");
  private final byte[] data = new byte[2 * RimoGetEvent.LENGTH];

  @Override
  public void rimoGet(RimoGetEvent rimoGetEventL, RimoGetEvent rimoGetEventR) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(data);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    rimoGetEventL.encode(byteBuffer);
    rimoGetEventR.encode(byteBuffer);
    publisher.accept(data, data.length);
  }
}
