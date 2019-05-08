// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class LinearAngleMappingTest extends TestCase {
  public void testSimple() {
    AngleMapping steerMapping = LinearAngleMapping.INSTANCE;
    Chop._12.requireClose(steerMapping.getAngleFromSCE(Quantity.of(10, "SCE")), Quantity.of(6, SI.ONE));
    Chop._12.requireClose(steerMapping.getSCEfromAngle(Quantity.of(6, "")), Quantity.of(10, "SCE"));
  }
}
