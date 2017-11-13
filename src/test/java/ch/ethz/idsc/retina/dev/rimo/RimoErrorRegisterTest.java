// code by jph
package ch.ethz.idsc.retina.dev.rimo;

import junit.framework.TestCase;

public class RimoErrorRegisterTest extends TestCase {
  public void testSimple() {
    assertEquals(RimoErrorRegister.values().length, 8);
  }
}
