// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import junit.framework.TestCase;

public class IgnitionModuleTest extends TestCase {
  public void testSimple() throws Exception {
    IgnitionModule ignitionModule = new IgnitionModule();
    ignitionModule.first();
    Thread.sleep(200);
    ignitionModule.last();
  }
}
