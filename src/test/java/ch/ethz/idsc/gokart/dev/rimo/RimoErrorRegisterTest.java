// code by jph
package ch.ethz.idsc.gokart.dev.rimo;

import junit.framework.TestCase;

public class RimoErrorRegisterTest extends TestCase {
  public void testSimple() {
    assertEquals(RimoErrorRegister.values().length, 8);
  }
}
