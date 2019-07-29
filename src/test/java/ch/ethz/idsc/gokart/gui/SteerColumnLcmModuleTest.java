// code by jph
package ch.ethz.idsc.gokart.gui;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import junit.framework.TestCase;

public class SteerColumnLcmModuleTest extends TestCase {
  public void testPeriod() throws Exception {
    ScalarUnaryOperator TO_MILLI_SECONDS = QuantityMagnitude.SI().in("ms");
    SteerColumnLcmModule gplm = new SteerColumnLcmModule();
    gplm.first();
    Scalar value = TO_MILLI_SECONDS.apply(gplm.getPeriod());
    assertEquals(value.number().longValue(), 10);
    gplm.runAlgo();
    gplm.last();
  }
}
