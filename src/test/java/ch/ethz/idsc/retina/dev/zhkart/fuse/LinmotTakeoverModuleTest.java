// code by jph
package ch.ethz.idsc.retina.dev.zhkart.fuse;

import junit.framework.TestCase;

public class LinmotTakeoverModuleTest extends TestCase {
  public void testSimple() throws Exception {
    LinmotTakeoverModule linmotTakeoverModule = new LinmotTakeoverModule();
    linmotTakeoverModule.first();
    assertFalse(linmotTakeoverModule.putEvent().isPresent());
    Thread.sleep(60);
    assertTrue(linmotTakeoverModule.putEvent().isPresent());
    linmotTakeoverModule.last();
  }
}
