// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.DoubleSummaryStatistics;
import java.util.List;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.red.ScalarSummaryStatistics;
import ch.ethz.idsc.tensor.sca.Clip;
import junit.framework.TestCase;

public class DubendorfCurveTest extends TestCase {
  public void testDistances() {
    Tensor CURVE = DubendorfCurve.HYPERLOOP_EIGHT;
    List<Integer> list = Dimensions.of(CURVE);
    assertEquals((int) list.get(1), 2);
    DoubleSummaryStatistics dss = Differences.of(CURVE).stream() //
        .map(Norm._2::ofVector) //
        .map(Scalar::number) //
        .mapToDouble(Number::doubleValue).summaryStatistics();
    // changed from 0.1 to 0.05 for eight demoday curve
    Clip clip = Clip.function(0.05, 0.4);
    clip.requireInside(RealScalar.of(dss.getMin()));
    clip.requireInside(RealScalar.of(dss.getMax()));
  }

  public void testHyperloop() {
    assertEquals(DubendorfCurve.HYPERLOOP_EIGHT.length(), 640);
  }

  private static void testCurve(Tensor curve) {
    List<Integer> list = Dimensions.of(curve);
    assertEquals((int) list.get(1), 2);
    ScalarSummaryStatistics sss = Differences.of(curve).stream() //
        .map(Norm._2::ofVector).collect(ScalarSummaryStatistics.collector());
    // changed from 0.1 to 0.05 for eight demoday curve
    Clip clip = Clip.function(0.05, 0.4);
    clip.requireInside(sss.getMin());
    clip.requireInside(sss.getMax());
  }

  public void testDistances2() {
    testCurve(DubendorfCurve.OVAL);
    testCurve(DubendorfCurve.DEMODAY_EIGHT);
    testCurve(DubendorfCurve.HYPERLOOP_EIGHT);
    testCurve(DubendorfCurve.HYPERLOOP_OVAL);
    testCurve(DubendorfCurve.TIRES_TRACK_A);
    testCurve(DubendorfCurve.TIRES_TRACK_B);
  }
}
