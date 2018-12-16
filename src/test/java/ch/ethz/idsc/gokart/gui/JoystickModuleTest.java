// code by jph
package ch.ethz.idsc.gokart.gui;

import ch.ethz.idsc.gokart.core.joy.SysidSignalsModule;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import junit.framework.TestCase;

public class JoystickModuleTest extends TestCase {
  public void testSimple() throws InterruptedException {
    for (Class<?> cls : RunTabbedTaskGui.MODULES_JOY)
      if (!cls.equals(SysidSignalsModule.class)) {
        ModuleAuto.INSTANCE.runOne(cls);
        Thread.sleep(50); // needs time to start thread that invokes first()
        ModuleAuto.INSTANCE.endOne(cls);
      } else
        System.out.println("skip " + cls);
  }
}
