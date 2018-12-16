// code by jph
package ch.ethz.idsc.gokart.dev.rimo;

import java.util.Objects;

import junit.framework.TestCase;

public class RimoPutTireTest extends TestCase {
  public void testSimple() {
    RimoPutTire rimoPutTire = new RimoPutTire(RimoPutTire.OPERATION, (short) 1, (short) 2);
    assertEquals(rimoPutTire.getRateRaw(), 1);
    assertEquals(rimoPutTire.getTorqueRaw(), 2);
    assertTrue(Objects.nonNull(rimoPutTire.toSDOHexString()));
    assertEquals(rimoPutTire.asVector().length(), 8);
  }
}
