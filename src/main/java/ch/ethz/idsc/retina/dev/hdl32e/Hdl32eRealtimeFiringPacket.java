// code by jph
package ch.ethz.idsc.retina.dev.hdl32e;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.IntRealtimeSleeper;

public class Hdl32eRealtimeFiringPacket implements Hdl32eRayDataListener {
  private final IntRealtimeSleeper rs;

  public Hdl32eRealtimeFiringPacket(double speed) {
    rs = new IntRealtimeSleeper(speed);
  }

  @Override
  public void timestamp(int usec, byte type, byte value) {
    rs.now(usec); // blocking
  }

  @Override
  public void scan(int rotational, ByteBuffer byteBuffer) {
  }
}
