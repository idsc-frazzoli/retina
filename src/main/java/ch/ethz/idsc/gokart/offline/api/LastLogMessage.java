// code by jph
package ch.ethz.idsc.gokart.offline.api;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.lcm.OfflineLogListener;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.tensor.Scalar;

/**
 * 
 */
public class LastLogMessage implements OfflineLogListener {
  public static Optional<ByteBuffer> of(File file, String channel) throws IOException {
    LastLogMessage lastLogMessage = new LastLogMessage(channel);
    OfflineLogPlayer.process(file, lastLogMessage);
    System.out.println("last of total=" + lastLogMessage.total());
    return lastLogMessage.optional;
  }

  // ---
  private final String channel;
  private Optional<ByteBuffer> optional = Optional.empty();
  private int total;

  private LastLogMessage(String channel) {
    this.channel = Objects.requireNonNull(channel);
  }

  @Override // from OfflineLogListener
  public void event(Scalar time, String string, ByteBuffer byteBuffer) {
    if (string.equals(channel)) {
      ++total;
      optional = Optional.of(byteBuffer);
    }
  }

  public int total() {
    return total;
  }
}
