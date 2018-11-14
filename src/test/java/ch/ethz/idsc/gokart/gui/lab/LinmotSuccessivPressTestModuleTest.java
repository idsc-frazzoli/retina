// code by mh
package ch.ethz.idsc.gokart.gui.lab;

import ch.ethz.idsc.tensor.RealScalar;
import junit.framework.TestCase;

public class LinmotSuccessivPressTestModuleTest extends TestCase {
  public void testSimple() throws Exception {
    LinmotSuccessivePressTestModule linmotPressTestModule = new LinmotSuccessivePressTestModule();
    linmotPressTestModule.first();
    linmotPressTestModule.pressAt(RealScalar.of(.2));
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
