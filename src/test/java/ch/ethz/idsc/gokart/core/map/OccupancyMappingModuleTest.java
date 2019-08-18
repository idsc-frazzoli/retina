// code by jph
package ch.ethz.idsc.gokart.core.map;

import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import junit.framework.TestCase;

public class OccupancyMappingModuleTest extends TestCase {
  public void testSimple() throws Exception {
    ModuleAuto.INSTANCE.runOne(OccupancyMappingModule.class);
    Thread.sleep(100);
    ModuleAuto.INSTANCE.endOne(OccupancyMappingModule.class);
  }
}
