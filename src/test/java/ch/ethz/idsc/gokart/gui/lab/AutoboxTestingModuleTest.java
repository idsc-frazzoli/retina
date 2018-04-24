// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import junit.framework.TestCase;

public class AutoboxTestingModuleTest extends TestCase {
  public void testSimple() throws Exception {
    AutoboxTestingModule autoboxTestingModule = new AutoboxTestingModule();
    autoboxTestingModule.first();
    Thread.sleep(200);
    autoboxTestingModule.last();
  }
}
