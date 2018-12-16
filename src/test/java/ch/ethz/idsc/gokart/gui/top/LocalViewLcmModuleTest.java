// code by jph
package ch.ethz.idsc.gokart.gui.top;

import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.tensor.mat.Det;
import ch.ethz.idsc.tensor.sca.Sign;
import junit.framework.TestCase;

public class LocalViewLcmModuleTest extends TestCase {
  public void testSimple() throws InterruptedException {
    ModuleAuto.INSTANCE.runOne(LocalViewLcmModule.class);
    Thread.sleep(200);
    ModuleAuto.INSTANCE.endOne(LocalViewLcmModule.class);
  }

  public void testDeterminant() {
    assertTrue(Sign.isNegative(Det.of(LocalViewLcmModule.MODEL2PIXEL)));
  }
}
