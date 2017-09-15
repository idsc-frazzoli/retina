// code by jph
package ch.ethz.idsc.retina.dev.misc;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.HexStrings;

public class MiscGetEvent {
  public static final int LENGTH = 44;
  public final byte emergency;
  private final float battery; // TODO should be raw value
  public final byte[] data;

  public MiscGetEvent(ByteBuffer byteBuffer) {
    emergency = byteBuffer.get();
    battery = byteBuffer.getFloat();
    int length = byteBuffer.remaining();
    data = new byte[length];
    byteBuffer.get(data);
  }

  public double steerBatteryVoltage() {
    return battery;
  }

  public String toInfoString() {
    return HexStrings.from(data);
  }
}
