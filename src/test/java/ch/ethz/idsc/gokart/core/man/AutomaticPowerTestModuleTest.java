// code by mh
package ch.ethz.idsc.gokart.core.man;

import junit.framework.TestCase;

public class AutomaticPowerTestModuleTest extends TestCase {
  public void testSimple() throws InterruptedException {
    AutomaticPowerTestModule testModule = new AutomaticPowerTestModule();
    testModule.first();
    Thread.sleep(1000);
    testModule.last();
  }
}
