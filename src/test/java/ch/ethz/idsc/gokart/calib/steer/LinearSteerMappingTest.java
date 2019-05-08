// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class LinearSteerMappingTest extends TestCase {
  public void testSimple() {
    SteerMapping steerMapping = LinearSteerMapping.INSTANCE;
    Chop._12.requireClose(steerMapping.getRatioFromSCE(Quantity.of(10, "SCE")), Quantity.of(6, SI.PER_METER));
    Chop._12.requireClose(steerMapping.getSCEfromRatio(Quantity.of(6, "m^-1")), Quantity.of(10, "SCE"));
  }
}
