// code by jph
package ch.ethz.idsc.gokart.core.tvec;

import ch.ethz.idsc.owl.car.math.AngularSlip;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Sign;
import junit.framework.TestCase;

public class TorqueVectoringConfigTest extends TestCase {
  public void testSimple() {
    Sign.requirePositive(QuantityMagnitude.singleton("s^2*m^-1").apply(TorqueVectoringConfig.GLOBAL.staticCompensation));
    Sign.requirePositive(Magnitude.SECOND.apply(TorqueVectoringConfig.GLOBAL.dynamicCorrection));
    Sign.requirePositive(Magnitude.SECOND.apply(TorqueVectoringConfig.GLOBAL.ks));
  }

  public void testAngularSlip() {
    AngularSlip angularSlip = new AngularSlip( //
        Quantity.of(2, SI.VELOCITY), //
        Quantity.of(0.2, SI.PER_METER), //
        Quantity.of(0.5, SI.PER_SECOND));
    Chop._10.requireClose(TorqueVectoringConfig.GLOBAL.getDynamicAndStatic(angularSlip), RealScalar.of(0.17));
    Scalar scalar = TorqueVectoringConfig.GLOBAL.getPredictiveComponent(Quantity.of(2, "s^-2"));
    Magnitude.ONE.apply(scalar);
  }
}
