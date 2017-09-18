// code by jph
package ch.ethz.idsc.retina.dev.misc;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.HexStrings;

public class MiscGetEvent {
  // TODO NRJ not final, just we don't know how many bytes we need
  public static final int LENGTH = 44;
  // ---
  public final byte emergency;
  // TODO NRJ battery should be raw value from adc (probably a short instead of
  // float)
  private final float battery;
  /** collection of bytes received after battery value */
  private final byte[] remaining;

  public MiscGetEvent(ByteBuffer byteBuffer) {
    emergency = byteBuffer.get();
    battery = byteBuffer.getFloat();
    int length = byteBuffer.remaining();
    remaining = new byte[length];
    byteBuffer.get(remaining);
  }

  public double steerBatteryVoltage() {
    return battery;
  }

  public String getRemainingHex() {
    return HexStrings.from(remaining);
  }
}
