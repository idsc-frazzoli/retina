// code by gjoel
package ch.ethz.idsc.demo.jg.bumblebee.steer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collection;
import java.util.stream.Collectors;

import ch.ethz.idsc.demo.jg.bumblebee.serial.BBSerialSocket;

public class BBSteerSocket extends BBSerialSocket<BBSteerGetEvent, BBSteerPutEvent> {
  private static final String PORT = "COM6"; // "/dev/ttyUSB1";
  private static final int SEND_PERIOD_MS = 10;

  public static final BBSteerSocket INSTANCE = new BBSteerSocket();

  // ---

  private BBSteerSocket() {
    super(PORT);
    addPutProvider(BBSteerPutFallback.INSTANCE);
  }

  @Override // from SerialStringSocket
  protected long getPutPeriod_ms() {
    return SEND_PERIOD_MS;
  }

  @Override // from BBSerialSocket
  protected final String putMessage(BBSteerPutEvent putEvent) {
    return putEvent.asVector().stream().map(t -> t.Get().number().intValue()).map(String::valueOf).collect(Collectors.joining(" "));
  }

  @Override // from BBSerialSocket
  protected final BBSteerGetEvent createGetEvent(Collection<Integer> values) {
    byte buffer[] = new byte[BBSteerGetEvent.LENGTH];
    ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    values.stream().map(Integer::shortValue).forEach(byteBuffer::putShort);
    return new BBSteerGetEvent(byteBuffer);
  }
}
