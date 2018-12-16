// code by jph
package ch.ethz.idsc.gokart.dev.rimo;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class RimoRateControllerDuoTest extends TestCase {
  public void testNull() {
    RimoRateControllerWrap rrcw = new RimoRateControllerDuo();
    assertFalse(rrcw.iterate(RealScalar.ZERO, RealScalar.ZERO).isPresent());
  }

  public void testPresent() {
    RimoRateControllerWrap rrcw = new RimoRateControllerDuo();
    assertFalse(rrcw.iterate(Quantity.of(1, "rad*s^-1"), RealScalar.of(0.3)).isPresent());
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[48]);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    RimoGetEvent rimoGetEvent = RimoSocket.INSTANCE.createGetEvent(byteBuffer);
    rrcw.getEvent(rimoGetEvent);
    // RimoPutEvent rimoPutEvent = rrcw.iterate(Tensors.fromString("{1[rad*s^-1],2[rad*s^-1]}")).get();
    RimoPutEvent rimoPutEvent = rrcw.iterate(Quantity.of(1, "rad*s^-1"), RealScalar.of(0.3)).get();
    assertFalse(rimoPutEvent.putTireL.getTorqueRaw() == -rimoPutEvent.putTireR.getTorqueRaw());
    assertTrue(rimoPutEvent.putTireL.getTorqueRaw() < 0);
    assertTrue(rimoPutEvent.putTireR.getTorqueRaw() > 0);
  }

  public void testSymmetric() {
    RimoRateControllerWrap rrcw = new RimoRateControllerDuo();
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[48]);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    RimoGetEvent rimoGetEvent = RimoSocket.INSTANCE.createGetEvent(byteBuffer);
    rrcw.getEvent(rimoGetEvent);
    RimoPutEvent rimoPutEvent = rrcw.iterate(Quantity.of(20, "rad*s^-1"), RealScalar.of(0)).get();
    assertEquals(rimoPutEvent.putTireL.getTorqueRaw(), -rimoPutEvent.putTireR.getTorqueRaw());
    assertTrue(rimoPutEvent.putTireL.getTorqueRaw() < 0);
    assertTrue(rimoPutEvent.putTireR.getTorqueRaw() > 0);
  }

  public void testSlowdown() {
    RimoRateControllerWrap rrcw = new RimoRateControllerDuo();
    assertFalse(rrcw.iterate(Quantity.of(1, "rad*s^-1"), RealScalar.of(0.1)).isPresent());
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[48]);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    byteBuffer.putShort(2, (short) -1260);
    byteBuffer.putShort(2 + 24, (short) 1101);
    byteBuffer.position(0);
    RimoGetEvent rimoGetEvent = RimoSocket.INSTANCE.createGetEvent(byteBuffer);
    assertEquals(rimoGetEvent.getAngularRate_Y_pair(), Tensors.fromString("{21.0[rad*s^-1], 18.35[rad*s^-1]}"));
    rrcw.getEvent(rimoGetEvent);
    RimoPutEvent rimoPutEvent = rrcw.iterate(Quantity.of(1, "rad*s^-1"), RealScalar.of(0.1)).get();
    Scalar tL = rimoPutEvent.putTireL.getTorque();
    Scalar tR = rimoPutEvent.putTireR.getTorque();
    // because "duo" uses two PI controllers, the torques do not have the same absolute value
    assertFalse(tL.equals(tR.negate()));
    assertTrue(rimoPutEvent.putTireL.getTorqueRaw() > 0); // verify slow down
    assertTrue(rimoPutEvent.putTireR.getTorqueRaw() < 0); // verify slow down
  }
}
