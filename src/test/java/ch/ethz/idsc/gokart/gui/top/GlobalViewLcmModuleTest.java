// code by jph
package ch.ethz.idsc.gokart.gui.top;

import junit.framework.TestCase;

public class GlobalViewLcmModuleTest extends TestCase {
  public void testSimple() throws Exception {
    GlobalViewLcmModule globalViewLcmModule = new GlobalViewLcmModule();
    globalViewLcmModule.first();
    Thread.sleep(10000);
    globalViewLcmModule.last();
  }
}
