// code by jph
package ch.ethz.idsc.retina.dev.steer;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.HexStrings;

public class SteerGetEvent {
  public static final int LENGTH = 14;
  public final byte[] data = new byte[LENGTH];

  public SteerGetEvent(ByteBuffer byteBuffer) {
    byteBuffer.get(data);
  }

  public String toInfoString() {
    return HexStrings.from(data);
  }
}
