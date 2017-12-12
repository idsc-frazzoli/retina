// code by jph
package ch.ethz.idsc.retina.dev.rimo;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

import junit.framework.TestCase;

public class RimoGetTireTest extends TestCase {
  public void testSimple() {
    assertEquals(RimoGetTire.LENGTH, 24);
  }

  public void testConstructor() {
    ByteBuffer bb = ByteBuffer.wrap(new byte[48]);
    bb.order(ByteOrder.LITTLE_ENDIAN);
    bb.putShort(2, (short) -600);
    bb.putShort(2 + 24, (short) 300);
    RimoGetEvent rge = new RimoGetEvent(bb);
    assertTrue(Objects.nonNull(rge.getTireL.toInfoString()));
    assertEquals(rge.getTireL.getErrorCodeMasked() & 0xff000000, 0);
  }
}
