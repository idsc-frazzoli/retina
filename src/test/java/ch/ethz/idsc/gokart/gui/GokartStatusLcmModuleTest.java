// code by jph
package ch.ethz.idsc.gokart.gui;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import junit.framework.TestCase;

public class GokartStatusLcmModuleTest extends TestCase {
  public void testPeriod() throws Exception {
    ScalarUnaryOperator TO_MILLI_SECONDS = QuantityMagnitude.SI().in("ms");
    GokartStatusLcmModule gplm = new GokartStatusLcmModule();
    gplm.first();
    Scalar value = TO_MILLI_SECONDS.apply(gplm.getPeriod());
    assertEquals(value.number().longValue(), 10);
    gplm.runAlgo();
    gplm.last();
  }
}
