// code by jph
package ch.ethz.idsc.gokart.offline.pose;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.AngleVector;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class LapLogSplitPredicateTest extends TestCase {
  public void testSimple() {
    LapLogSplitPredicate lapLogSplitPredicate = new LapLogSplitPredicate( //
        Tensors.vector(41.6, 34.2).multiply(Quantity.of(1, SI.METER)), //
        AngleVector.of(RealScalar.of(-2.25)));
    Tensor newPos = Tensors.vector(20, 20).multiply(Quantity.of(1, SI.METER));
    Tensor oldPos = Tensors.vector(50, 40).multiply(Quantity.of(1, SI.METER));
    assertFalse(lapLogSplitPredicate.getLineTrigger(newPos, oldPos));
    assertFalse(lapLogSplitPredicate.getLineTrigger(newPos, newPos));
    assertFalse(lapLogSplitPredicate.getLineTrigger(oldPos, oldPos));
    assertTrue(lapLogSplitPredicate.getLineTrigger(oldPos, newPos));
  }
}
