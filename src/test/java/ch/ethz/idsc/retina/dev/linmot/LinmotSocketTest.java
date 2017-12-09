// code by jph
package ch.ethz.idsc.retina.dev.linmot;

import junit.framework.TestCase;

public class LinmotSocketTest extends TestCase {
  public void testSimple() {
    LinmotSocket.INSTANCE.getPutProviderDesc();
  }

  public void testPeriod() {
    assertEquals(LinmotSocket.INSTANCE.getPeriod_ms(), 20);
  }
}
