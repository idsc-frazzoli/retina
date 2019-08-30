// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import java.io.IOException;

import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import junit.framework.TestCase;

public class SteerFeedForwardConfigTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    ScalarUnaryOperator series = Serialization.copy(SteerFeedForwardConfig.GLOBAL.series());
    Scalar sct = series.apply(Quantity.of(0.3, "SCE"));
    Clips.interval(0.2, 0.3).requireInside(SteerPutEvent.RTORQUE.apply(sct));
  }

  public void testOdd() {
    ScalarUnaryOperator series = SteerFeedForwardConfig.GLOBAL.series();
    Distribution distribution = UniformDistribution.of(Quantity.of(-0.2, "SCE"), Quantity.of(0.4, "SCE"));
    for (int count = 0; count < 100; ++count) {
      Scalar scalar = RandomVariate.of(distribution);
      Chop._07.requireClose(series.apply(scalar).negate(), series.apply(scalar.negate()));
    }
  }
}
