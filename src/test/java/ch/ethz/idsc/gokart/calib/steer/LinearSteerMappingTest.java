// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class LinearSteerMappingTest extends TestCase {
  public void testSimple() {
    SteerMapping steerMapping = LinearSteerMapping.INSTANCE;
    Chop._12.requireClose(steerMapping.getAngleFromSCE(Quantity.of(10, "SCE")), RealScalar.of(6));
    Chop._12.requireClose(steerMapping.getSCEfromAngle(Quantity.of(6, "")), Quantity.of(10, "SCE"));
  }
}
