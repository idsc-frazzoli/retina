// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import ch.ethz.idsc.tensor.RealScalar;
import junit.framework.TestCase;

public class LinmotPressTestModuleTest extends TestCase {
  public void testSimple() throws Exception {
    LinmotPressTestModule linmotPressTestModule = new LinmotPressTestModule();
    linmotPressTestModule.first();
    linmotPressTestModule.pressAt(RealScalar.of(.2));
    linmotPressTestModule.last();
  }
}
