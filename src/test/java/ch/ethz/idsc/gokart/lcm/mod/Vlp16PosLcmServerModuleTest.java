// code by jph
package ch.ethz.idsc.gokart.lcm.mod;

import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import junit.framework.TestCase;

public class Vlp16PosLcmServerModuleTest extends TestCase {
  public void testSimple() throws Exception {
    ModuleAuto.INSTANCE.runOne(Vlp16PosLcmServerModule.class);
    ModuleAuto.INSTANCE.endOne(Vlp16PosLcmServerModule.class);
  }
}
