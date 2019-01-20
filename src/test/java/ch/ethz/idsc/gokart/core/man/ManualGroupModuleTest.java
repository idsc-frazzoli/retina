// code by jph
package ch.ethz.idsc.gokart.core.man;

import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import junit.framework.TestCase;

public class ManualGroupModuleTest extends TestCase {
  public void testSize() {
    assertEquals(new ManualGroupModule().modules().size(), 3);
  }

  public void testSimple() throws Exception {
    ModuleAuto.INSTANCE.runOne(ManualGroupModule.class);
    Thread.sleep(50);
    ModuleAuto.INSTANCE.endOne(ManualGroupModule.class);
  }
}
