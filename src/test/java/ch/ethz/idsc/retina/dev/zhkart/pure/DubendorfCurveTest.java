// code by jph
package ch.ethz.idsc.retina.dev.zhkart.pure;

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
    // System.out.println(list);
    DoubleSummaryStatistics dss = Differences.of(PurePursuitModule.CURVE).stream() //
        .map(Norm._2::ofVector) //
        .map(Scalar::number) //
        .mapToDouble(Number::doubleValue).summaryStatistics();
    Clip clip = Clip.function(0.1, 0.4);
    clip.isInsideElseThrow(RealScalar.of(dss.getMin()));
    clip.isInsideElseThrow(RealScalar.of(dss.getMax()));
  }
}
