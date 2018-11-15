// code by mh
package ch.ethz.idsc.gokart.gui.lab;

import junit.framework.TestCase;

public class LinmotSuccessivPressTestModuleTest extends TestCase {
  public void testSimple() throws Exception {
    LinmotConstantPressTestModule linmotPressTestModule = new LinmotConstantPressTestModule();
    linmotPressTestModule.first();
    linmotPressTestModule.next();
    Thread.sleep(100);
    linmotPressTestModule.previous();
    Thread.sleep(100);
    linmotPressTestModule.previous();
    Thread.sleep(100);
    linmotPressTestModule.next();
    Thread.sleep(100);
    linmotPressTestModule.last();
  }
}
