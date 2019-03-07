// code by jph
package ch.ethz.idsc.gokart.dev.rimo;

import junit.framework.TestCase;

public class RimoPutFallbackTest extends TestCase {
  public void testRegistered() {
    assertEquals(RimoPutFallback.INSTANCE.putEvent().get(), RimoPutEvent.PASSIVE);
  }
}
