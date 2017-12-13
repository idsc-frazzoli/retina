// code by jph
package ch.ethz.idsc.retina.dev.rimo;

import java.util.Objects;

import junit.framework.TestCase;

public class RimoPutTireTest extends TestCase {
  public void testSimple() {
    RimoPutTire rimoPutTire = new RimoPutTire(RimoPutTire.OPERATION, (short) 0, (short) 1);
    assertTrue(Objects.nonNull(rimoPutTire.toSDOHexString()));
  }
}
