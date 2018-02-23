// code by edo
package ch.ethz.idsc.owl.car.drift;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class DriftParametersTest extends TestCase {
  public void testFzF() {
    DriftParameters driftParameters = new DriftParameters();
    Scalar FzF = driftParameters.Fz_F;
    Scalar FzR = driftParameters.Fz_R;
    assertTrue(Chop._10.close(FzF, RealScalar.of(9020.278144329897)));
    assertTrue(Chop._10.close(FzR, RealScalar.of(4831.441855670103)));
  }
}
