// code by jph
package ch.ethz.idsc.gokart.gui;

import ch.ethz.idsc.gokart.core.fuse.SteerCalibrationWatchdog;
import junit.framework.TestCase;

public class RunTabbedTaskGuiTest extends TestCase {
  public void testSimple() {
    assertTrue(RunTabbedTaskGui.MODULES_DEV.contains(SteerCalibrationWatchdog.class));
  }
}
