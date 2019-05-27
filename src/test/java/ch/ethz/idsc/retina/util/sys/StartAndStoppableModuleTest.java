// code by jph
package ch.ethz.idsc.retina.util.sys;

import junit.framework.TestCase;

public class StartAndStoppableModuleTest extends TestCase {
  public void testFailNull() {
    try {
      new StartAndStoppableModule(null);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
