// code by mh
package ch.ethz.idsc.gokart.core.man;

import junit.framework.TestCase;

public class AutomaticPowerTestModuleTest extends TestCase {
  public void testSimple() {
    AutomaticPowerTestModule testModule = new AutomaticPowerTestModule();
    testModule.first();
    try {
      Thread.sleep(100000);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    testModule.last();
  }
}
