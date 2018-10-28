// code by jph
package ch.ethz.idsc.gokart.gui.top;

import junit.framework.TestCase;

public class PresenterLcmModuleTest extends TestCase {
  public void testSimple() throws Exception {
    PresenterLcmModule plm = new PresenterLcmModule();
    plm.first();
    Thread.sleep(2000);
    plm.last();
  }
}
