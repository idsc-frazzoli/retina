// code by jph
package ch.ethz.idsc.retina.dev.hdl32e;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.RealtimeSleeper;

public class RealtimeFiringPacket implements FiringPacketInterface {
  private final RealtimeSleeper rs;

  public RealtimeFiringPacket(double speed) {
    rs = new RealtimeSleeper(speed);
  }

  @Override
  public void process(int firing, int rotational, ByteBuffer byteBuffer) {
  }

  @Override
  public void status(int usec, byte type, byte value) {
    rs.now(usec);
  }
}
