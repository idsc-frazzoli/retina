// code by jph
package ch.ethz.idsc.gokart.dev.rimo;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
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
    assertEquals(rimoPutEvent.putTireL.getTorqueRaw(), -rimoPutEvent.putTireR.getTorqueRaw());
    assertTrue(rimoPutEvent.putTireL.getTorqueRaw() <= 0);
    assertTrue(rimoPutEvent.putTireR.getTorqueRaw() >= 0);
  }

  public void testSymmetric() {
    RimoRateControllerWrap rrcw = new RimoRateControllerUno();
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[48]);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    RimoGetEvent rimoGetEvent = RimoSocket.INSTANCE.createGetEvent(byteBuffer);
    rrcw.getEvent(rimoGetEvent);
    RimoPutEvent rimoPutEvent = rrcw.iterate(Quantity.of(20, "rad*s^-1"), RealScalar.of(0)).get();
    assertEquals(rimoPutEvent.putTireL.getTorqueRaw(), -rimoPutEvent.putTireR.getTorqueRaw());
    assertTrue(rimoPutEvent.putTireL.getTorqueRaw() <= 0);
    assertTrue(rimoPutEvent.putTireR.getTorqueRaw() >= 0);
  }

  public void testSlowdown() {
    RimoRateControllerWrap rrcw = new RimoRateControllerUno();
    assertFalse(rrcw.iterate(Quantity.of(1, "rad*s^-1"), RealScalar.of(0.1)).isPresent());
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[48]);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    byteBuffer.putShort(2, (short) -126);
    byteBuffer.putShort(2 + 24, (short) 120);
    byteBuffer.position(0);
    RimoGetEvent rimoGetEvent = RimoSocket.INSTANCE.createGetEvent(byteBuffer);
    assertEquals(rimoGetEvent.getAngularRate_Y_pair(), Tensors.fromString("{2.1[rad*s^-1], 2.0[rad*s^-1]}"));
    rrcw.getEvent(rimoGetEvent);
    // RimoPutEvent rimoPutEvent = rrcw.iterate(Tensors.fromString("{1[rad*s^-1],2[rad*s^-1]}")).get();
    RimoPutEvent rimoPutEvent = rrcw.iterate(Quantity.of(1, "rad*s^-1"), RealScalar.of(0.1)).get();
    // because "uno" uses a single PI controller, the torques have the same absolute value
    assertEquals(rimoPutEvent.putTireL.getTorqueRaw(), -rimoPutEvent.putTireR.getTorqueRaw());
    // System.out.println(rimoPutEvent.putTireL.getTorqueRaw());
    // System.out.println(rimoPutEvent.putTireR.getTorqueRaw());
    assertTrue(rimoPutEvent.putTireL.getTorqueRaw() >= -60);
    assertTrue(rimoPutEvent.putTireR.getTorqueRaw() <= 60);
  }
}
