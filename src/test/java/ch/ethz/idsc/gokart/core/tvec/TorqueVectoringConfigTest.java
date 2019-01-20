// code by jph
package ch.ethz.idsc.gokart.core.tvec;

import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.sca.Sign;
import junit.framework.TestCase;

public class TorqueVectoringConfigTest extends TestCase {
  public void testSimple() {
    Sign.requirePositive(QuantityMagnitude.singleton("s^2*m^-1").apply(TorqueVectoringConfig.GLOBAL.staticCompensation));
    Sign.requirePositive(Magnitude.SECOND.apply(TorqueVectoringConfig.GLOBAL.dynamicCorrection));
    Sign.requirePositive(Magnitude.SECOND.apply(TorqueVectoringConfig.GLOBAL.ks));
  }
}
