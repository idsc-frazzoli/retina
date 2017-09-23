// code by jph
package ch.ethz.idsc.retina.lcm.autobox;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoGetListener;
import ch.ethz.idsc.retina.dev.rimo.RimoGetTire;
import ch.ethz.idsc.retina.lcm.BinaryBlobPublisher;

public enum RimoLcmServer implements RimoGetListener {
  INSTANCE;
  // ---
  private final BinaryBlobPublisher publisher = new BinaryBlobPublisher("autobox.rimo.get");
  private final byte[] data = new byte[2 * RimoGetTire.LENGTH];

  @Override
  public void digest(RimoGetEvent rimoGetEvent) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(data);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    rimoGetEvent.getL.encode(byteBuffer);
    rimoGetEvent.getR.encode(byteBuffer);
    publisher.accept(data, data.length);
  }
}
