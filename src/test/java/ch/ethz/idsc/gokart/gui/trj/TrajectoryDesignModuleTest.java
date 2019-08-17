// code by jph
package ch.ethz.idsc.gokart.gui.trj;

import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import junit.framework.TestCase;

public class TrajectoryDesignModuleTest extends TestCase {
  public void testSimple() throws Exception {
    ModuleAuto.INSTANCE.runOne(TrajectoryDesignModule.class);
    Thread.sleep(200);
    ModuleAuto.INSTANCE.endOne(TrajectoryDesignModule.class);
  }
}
