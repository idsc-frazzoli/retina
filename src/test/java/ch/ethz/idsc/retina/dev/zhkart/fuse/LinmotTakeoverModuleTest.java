// code by jph
package ch.ethz.idsc.retina.dev.zhkart.fuse;

import junit.framework.TestCase;

public class LinmotTakeoverModuleTest extends TestCase {
  public void testFirstLast() throws Exception {
    LinmotTakeoverModule linmotTakeoverModule = new LinmotTakeoverModule();
    linmotTakeoverModule.first();
    linmotTakeoverModule.last();
  }

  public void testSimple() throws Exception {
    LinmotTakeoverModule linmotTakeoverModule = new LinmotTakeoverModule();
    assertFalse(linmotTakeoverModule.putEvent().isPresent());
    Thread.sleep(60);
    assertTrue(linmotTakeoverModule.putEvent().isPresent());
  }
}
