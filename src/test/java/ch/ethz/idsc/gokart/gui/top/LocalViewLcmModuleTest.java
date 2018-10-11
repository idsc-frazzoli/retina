// code by jph
package ch.ethz.idsc.gokart.gui.top;

import ch.ethz.idsc.retina.sys.ModuleAuto;
import junit.framework.TestCase;

public class LocalViewLcmModuleTest extends TestCase {
  public void testSimple() throws InterruptedException {
    ModuleAuto.INSTANCE.runOne(LocalViewLcmModule.class);
    Thread.sleep(200);
    ModuleAuto.INSTANCE.terminateOne(LocalViewLcmModule.class);
  }
}
