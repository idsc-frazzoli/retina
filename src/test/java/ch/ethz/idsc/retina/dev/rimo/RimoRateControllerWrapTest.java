// code by jph
package ch.ethz.idsc.retina.dev.rimo;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class RimoRateControllerWrapTest extends TestCase {
  public void testNull() {
    RimoRateControllerWrap rrcw = new RimoRateControllerWrap();
    assertFalse(rrcw.iterate(Tensors.vector(0, 0)).isPresent());
  }

  public void testPresent() {
    RimoRateControllerWrap rrcw = new RimoRateControllerWrap();
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[48]);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    RimoGetEvent rimoGetEvent = RimoSocket.INSTANCE.createGetEvent(byteBuffer);
    rrcw.getEvent(rimoGetEvent);
    RimoPutEvent rimoPutEvent = rrcw.iterate(Tensors.fromString("{1[rad*s^-1],2[rad*s^-1]}")).get();
    assertTrue(rimoPutEvent.putL.getTorqueRaw() < 0);
    assertTrue(0 < rimoPutEvent.putR.getTorqueRaw());
  }
}
