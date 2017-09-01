// code by jph
package ch.ethz.idsc.retina.dev.lidar.urg04lx;

import java.nio.ByteBuffer;

public class Urg04lxEvent {
  public static Urg04lxEvent fromString(String line) {
    int index = line.indexOf('{');
    long timestamp = Long.parseLong(line.substring(3, index)); // <- removes "URG" prefix from line
    String[] split = line.substring(index + 1, line.length() - 1).split(",");
    short[] range = new short[split.length];
    for (int count = 0; count < split.length; ++count)
      range[count] = Short.parseShort(split[count]);
    return new Urg04lxEvent(timestamp, range);
  }

  @Deprecated // TODO implementation does not belong here
  public static Urg04lxEvent fromByteBuffer(ByteBuffer byteBuffer) {
    long timestamp = byteBuffer.getLong();
    short[] range = new short[Urg04lxDevice.POINTS];
    for (int count = 0; count < range.length; ++count)
      range[count] = byteBuffer.getShort();
    return new Urg04lxEvent(timestamp, range);
  }

  // ---
  public final long timestamp;
  public final short[] range;

  public Urg04lxEvent(long timestamp, short[] range) {
    this.timestamp = timestamp;
    this.range = range;
  }
}
