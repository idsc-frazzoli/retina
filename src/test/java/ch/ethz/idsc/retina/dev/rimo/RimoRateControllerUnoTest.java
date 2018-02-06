// code by jph
package ch.ethz.idsc.retina.dev.rimo;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class RimoRateControllerUnoTest extends TestCase {
  public void testNull() {
    RimoRateControllerWrap rrcw = new RimoRateControllerUno();
    assertFalse(rrcw.iterate(RealScalar.ZERO, RealScalar.ZERO).isPresent());
  }

  public void testPresent() {
    RimoRateControllerWrap rrcw = new RimoRateControllerUno();
    assertFalse(rrcw.iterate(Quantity.of(1, "rad*s^-1"), RealScalar.of(0.3)).isPresent());
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[48]);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    RimoGetEvent rimoGetEvent = RimoSocket.INSTANCE.createGetEvent(byteBuffer);
    rrcw.getEvent(rimoGetEvent);
    // RimoPutEvent rimoPutEvent = rrcw.iterate(Tensors.fromString("{1[rad*s^-1],2[rad*s^-1]}")).get();
    RimoPutEvent rimoPutEvent = rrcw.iterate(Quantity.of(1, "rad*s^-1"), RealScalar.of(0.3)).get();
    assertEquals(rimoPutEvent.putL.getTorqueRaw(), -rimoPutEvent.putR.getTorqueRaw());
    assertTrue(rimoPutEvent.putL.getTorqueRaw() < 0);
    assertTrue(rimoPutEvent.putR.getTorqueRaw() > 0);
  }

  public void testSymmetric() {
    RimoRateControllerWrap rrcw = new RimoRateControllerUno();
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[48]);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    RimoGetEvent rimoGetEvent = RimoSocket.INSTANCE.createGetEvent(byteBuffer);
    rrcw.getEvent(rimoGetEvent);
    RimoPutEvent rimoPutEvent = rrcw.iterate(Quantity.of(20, "rad*s^-1"), RealScalar.of(0)).get();
    assertEquals(rimoPutEvent.putL.getTorqueRaw(), -rimoPutEvent.putR.getTorqueRaw());
    assertTrue(rimoPutEvent.putL.getTorqueRaw() < 0);
    assertTrue(rimoPutEvent.putR.getTorqueRaw() > 0);
  }
}
