// code by jph
package ch.ethz.idsc.gokart.gui;

import ch.ethz.idsc.gokart.core.fuse.MiscEmergencyWatchdog;
import ch.ethz.idsc.gokart.core.fuse.SteerCalibrationWatchdog;
import ch.ethz.idsc.retina.sys.ModuleAuto;
import junit.framework.TestCase;

public class RunTabbedTaskGuiTest extends TestCase {
  public void testSimple() {
    assertTrue(RunTabbedTaskGui.MODULES_DEV.contains(SteerCalibrationWatchdog.class));
    assertTrue(RunTabbedTaskGui.MODULES_DEV.contains(MiscEmergencyWatchdog.class));
  }

  public void testAutStartStop() throws Exception {
    for (Class<?> module : RunTabbedTaskGui.MODULES_AUT) {
      ModuleAuto.INSTANCE.runOne(module);
      Thread.sleep(100);
      ModuleAuto.INSTANCE.terminateOne(module);
    }
  }
}
