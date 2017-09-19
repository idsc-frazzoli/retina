// code by jph
package ch.ethz.idsc.retina.dev.steer;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.HexStrings;

/** information received from micro-autobox about steering */
public class SteerGetEvent {
  // TODO NRJ not final number, but we don't know how many we need and what the bytes mean
  public static final int LENGTH = 44;
  // ---
  public final byte[] remaining;

  public SteerGetEvent(ByteBuffer byteBuffer) {
    int length = byteBuffer.remaining();
    remaining = new byte[length];
    byteBuffer.get(remaining);
  }

  public void encode(ByteBuffer byteBuffer) {
  }

  public String getRemainingInHex() {
    return HexStrings.from(remaining);
  }
}
