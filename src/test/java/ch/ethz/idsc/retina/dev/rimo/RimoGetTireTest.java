// code by jph
package ch.ethz.idsc.retina.dev.rimo;

import java.util.Objects;

import junit.framework.TestCase;

public class RimoGetTireTest extends TestCase {
  public void testSimple() {
    assertEquals(RimoGetTire.LENGTH, 24);
  }

  public void testConstructor() {
    RimoGetEvent rge = RimoGetEvents.create(600, 300);
    assertTrue(Objects.nonNull(rge.getTireL.toInfoString()));
    assertEquals(rge.getTireL.getErrorCodeMasked() & 0xff000000, 0);
  }
}
