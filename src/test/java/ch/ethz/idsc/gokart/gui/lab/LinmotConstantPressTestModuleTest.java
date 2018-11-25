// code by mh
package ch.ethz.idsc.gokart.gui.lab;

import junit.framework.TestCase;

public class LinmotConstantPressTestModuleTest extends TestCase {
  public void testSimple() throws Exception {
    LinmotConstantPressTestModule linmotConstantPressTestLinmot = new LinmotConstantPressTestModule();
    linmotConstantPressTestLinmot.first();
    Thread.sleep(10000);
    linmotConstantPressTestLinmot.last();
  }
}
