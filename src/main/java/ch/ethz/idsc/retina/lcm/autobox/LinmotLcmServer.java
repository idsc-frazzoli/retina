// code by jph
package ch.ethz.idsc.retina.lcm.autobox;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.retina.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotGetListener;
import ch.ethz.idsc.retina.lcm.BinaryBlobPublisher;

public enum LinmotLcmServer implements LinmotGetListener {
  INSTANCE;
  // ---
  private final BinaryBlobPublisher publisher = new BinaryBlobPublisher("autobox.linmot.get");
  private final byte[] data = new byte[LinmotGetEvent.LENGTH];

  @Override
  public void getEvent(LinmotGetEvent linmotGetEvent) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(data);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    linmotGetEvent.encode(byteBuffer);
    publisher.accept(data, data.length);
  }
}
