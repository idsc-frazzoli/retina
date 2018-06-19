// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import junit.framework.TestCase;

public class AutoboxCompactModuleTest extends TestCase {
  public void testSimple() throws Exception {
    AutoboxCompactModule autoboxCompactModule = new AutoboxCompactModule();
    autoboxCompactModule.first();
    Thread.sleep(200);
    autoboxCompactModule.last();
  }
}
