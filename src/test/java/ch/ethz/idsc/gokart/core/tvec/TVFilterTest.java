// code by jph
package ch.ethz.idsc.gokart.core.tvec;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class TVFilterTest extends TestCase {
  public void testFilter() {
    ImprovedNormalizedPredictiveTorqueVectoring inpdv = //
        new ImprovedNormalizedPredictiveTorqueVectoring(TorqueVectoringConfig.GLOBAL);
    Scalar acc0 = inpdv.estimateRotationAcceleration(Quantity.of(1, "s^-1"), 0.1);
    Chop._13.requireClose(acc0, Quantity.of(0, SI.ANGULAR_ACCELERATION));
    Scalar acc1 = inpdv.estimateRotationAcceleration(Quantity.of(1.2, "s^-1"), 0.1);
    Chop._13.requireClose(acc1, Quantity.of(1, SI.ANGULAR_ACCELERATION));
    Scalar acc2 = inpdv.estimateRotationAcceleration(Quantity.of(1.3, "s^-1"), 0.1);
    Chop._13.requireClose(acc2, Quantity.of(1, SI.ANGULAR_ACCELERATION));
    Scalar acc3 = inpdv.estimateRotationAcceleration(Quantity.of(1.5, "s^-1"), 0.1);
    Chop._13.requireClose(acc3, Quantity.of(1.5, SI.ANGULAR_ACCELERATION));
    Scalar acc4 = inpdv.estimateRotationAcceleration(Quantity.of(1.2, "s^-1"), 0.2);
    Chop._13.requireClose(acc4, Quantity.of(0, SI.ANGULAR_ACCELERATION));
    Scalar acc5 = inpdv.estimateRotationAcceleration(Quantity.of(1.3, "s^-1"), 0.1);
    Chop._13.requireClose(acc5, Quantity.of(0.5, SI.ANGULAR_ACCELERATION));
  }
}
