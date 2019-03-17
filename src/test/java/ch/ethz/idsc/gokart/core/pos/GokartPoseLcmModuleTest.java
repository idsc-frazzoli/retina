// code by jph
package ch.ethz.idsc.gokart.core.pos;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import junit.framework.TestCase;

public class GokartPoseLcmModuleTest extends TestCase {
  public void testSimple() throws Exception {
    PoseLcmServerModule gplm = new PoseLcmServerModule();
    gplm.first();
    gplm.runAlgo();
    gplm.last();
  }

  public void testPeriod() throws Exception {
    ScalarUnaryOperator TO_MILLI_SECONDS = QuantityMagnitude.SI().in("ms");
    PoseLcmServerModule gplm = new PoseLcmServerModule();
    Scalar value = TO_MILLI_SECONDS.apply(gplm.getPeriod());
    assertEquals(value.number().longValue(), 20);
  }
}
