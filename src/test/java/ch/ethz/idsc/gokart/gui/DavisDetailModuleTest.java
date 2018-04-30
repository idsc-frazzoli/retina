// code by jph
package ch.ethz.idsc.gokart.gui;

import junit.framework.TestCase;

public class DavisDetailModuleTest extends TestCase {
  public void testSimple() throws Exception {
    DavisDetailModule ddm = new DavisDetailModule();
    ddm.first();
    Thread.sleep(100);
    ddm.last();
  }
}
