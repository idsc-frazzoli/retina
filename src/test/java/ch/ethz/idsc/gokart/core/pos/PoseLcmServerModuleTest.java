// code by jph
package ch.ethz.idsc.gokart.core.pos;

import ch.ethz.idsc.gokart.core.slam.LidarLocalizationModule;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import junit.framework.TestCase;

public class PoseLcmServerModuleTest extends TestCase {
  public void testSimple() throws Exception {
    ModuleAuto.INSTANCE.runOne(LidarLocalizationModule.class);
    PoseLcmServerModule poseLcmServerModule = new PoseLcmServerModule();
    poseLcmServerModule.first();
    poseLcmServerModule.runAlgo();
    poseLcmServerModule.last();
    ModuleAuto.INSTANCE.endOne(LidarLocalizationModule.class);
  }

  public void testPeriod() throws Exception {
    ScalarUnaryOperator TO_MILLI_SECONDS = QuantityMagnitude.SI().in("ms");
    PoseLcmServerModule poseLcmServerModule = new PoseLcmServerModule();
    Scalar value = TO_MILLI_SECONDS.apply(poseLcmServerModule.getPeriod());
    assertEquals(value.number().longValue(), 20);
  }
}
