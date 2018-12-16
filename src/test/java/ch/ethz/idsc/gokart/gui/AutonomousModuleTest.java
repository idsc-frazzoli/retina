// code by jph
package ch.ethz.idsc.gokart.gui;

import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import junit.framework.TestCase;

public class AutonomousModuleTest extends TestCase {
  public void testSimple() throws InterruptedException {
    for (Class<?> cls : RunTabbedTaskGui.MODULES_AUT) {
      ModuleAuto.INSTANCE.runOne(cls);
      Thread.sleep(150); // needs time to start thread that invokes first()
      ModuleAuto.INSTANCE.endOne(cls);
    }
  }
}
