// code by jph
package ch.ethz.idsc.gokart.core.pos;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import junit.framework.TestCase;

public class GokartPoseLcmModuleTest extends TestCase {
  public void testSimple() throws Exception {
    GokartPoseLcmModule gplm = new GokartPoseLcmModule();
    gplm.first();
    gplm.runAlgo();
    gplm.last();
  }

  public void testPeriod() throws Exception {
    ScalarUnaryOperator TO_MILLI_SECONDS = QuantityMagnitude.SI().in("ms");
    GokartPoseLcmModule gplm = new GokartPoseLcmModule();
    Scalar value = TO_MILLI_SECONDS.apply(gplm.getPeriod());
    assertEquals(value.number().longValue(), 20);
  }
}
