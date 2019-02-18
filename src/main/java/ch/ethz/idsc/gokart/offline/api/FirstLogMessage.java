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

public class FirstLogMessage implements OfflineLogListener {
  public static Optional<ByteBuffer> of(File file, String channel) throws IOException {
    FirstLogMessage firstLogMessage = new FirstLogMessage(channel);
    try {
      OfflineLogPlayer.process(file, firstLogMessage);
    } catch (RuntimeException runtimeException) {
      // ---
    }
    return firstLogMessage.optional;
  }

  // ---
  private final String channel;
  private Optional<ByteBuffer> optional = Optional.empty();

  private FirstLogMessage(String channel) {
    this.channel = Objects.requireNonNull(channel);
  }

  @Override // from OfflineLogListener
  public void event(long utime, Scalar time, String string, ByteBuffer byteBuffer) {
    if (string.equals(channel)) {
      optional = Optional.of(byteBuffer);
      throw new RuntimeException();
    }
  }
}
