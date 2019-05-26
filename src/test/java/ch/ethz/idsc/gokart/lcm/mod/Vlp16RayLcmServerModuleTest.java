// code by jph
package ch.ethz.idsc.gokart.lcm.mod;

import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import junit.framework.TestCase;

public class Vlp16RayLcmServerModuleTest extends TestCase {
  public void testSimple() throws Exception {
    ModuleAuto.INSTANCE.runOne(Vlp16RayLcmServerModule.class);
    ModuleAuto.INSTANCE.endOne(Vlp16RayLcmServerModule.class);
  }
}
