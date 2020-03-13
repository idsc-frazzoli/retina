// code by gjoel
package ch.ethz.idsc.gokart.dev.led;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.data.DataEvent;
import ch.ethz.idsc.tensor.Tensor;

public class LEDPutEvent extends DataEvent {
  public static LEDPutEvent from(LEDStatus status) {
    return new LEDPutEvent(status);
  }

  // ---

  public final LEDStatus status;

  private LEDPutEvent(LEDStatus status) {
    this.status = status;
  }

  @Override // from BufferInsertable
  public void insert(ByteBuffer byteBuffer) {
    for (int i : status.asArray())
      byteBuffer.putInt(i);
  }

  @Override // from BufferInsertable
  public int length() {
    return status.asArray().length * 4;
  }

  @Override // from OfflineVectorInterface
  public Tensor asVector() {
    return status.asVector();
  }
}
