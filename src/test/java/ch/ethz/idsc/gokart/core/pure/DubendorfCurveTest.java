// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.DoubleSummaryStatistics;
import java.util.List;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Clip;
import junit.framework.TestCase;

public class DubendorfCurveTest extends TestCase {
  public void testDistances() {
    List<Integer> list = Dimensions.of(PurePursuitModule.CURVE);
    assertEquals((int) list.get(1), 2);
    DoubleSummaryStatistics dss = Differences.of(PurePursuitModule.CURVE).stream() //
        .map(Norm._2::ofVector) //
        .map(Scalar::number) //
        .mapToDouble(Number::doubleValue).summaryStatistics();
    // changed from 0.1 to 0.05 for eight demoday curve
    Clip clip = Clip.function(0.05, 0.4);
    clip.requireInside(RealScalar.of(dss.getMin()));
    clip.requireInside(RealScalar.of(dss.getMax()));
  }
}
